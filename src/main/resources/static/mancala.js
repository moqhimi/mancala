(function(){

    // this class represents the UI part of the game and have ajax handlers to communicate with server
    var Mancala = function(){

        // initial state before get states from server. it is due to initial drawing of board and pits.
        this.initial_state = {
            "states": [
                {
                    "player1":{
                        "pit" : {
                            "pit1" : 0,
                            "pit2" : 0,
                            "pit3" : 0,
                            "pit4" : 0,
                            "pit5" : 0,
                            "pit6" : 0
                        },
                        "largePit": 0
                    },
                    "player2":{
                        "pit" : {
                            "pit1" : 0,
                            "pit2" : 0,
                            "pit3" : 0,
                            "pit4" : 0,
                            "pit5" : 0,
                            "pit6" : 0
                        },
                        "largePit": 0
                    }
                }
            ],
            "status" : -1,
            "turn": "player1"
        };

        // override objects to not have rotation point and be centered scaling (the coordinate of scaling is based on center of objects)
        fabric.Object.prototype.set({
            hasRotatingPoint:false,
            centeredScaling: true,
            lockScalingFlip: true,
        });

        // status of the game at any time
        var STATUS = {
            INIT : 0,
            PLAY : 1,
            FINISH : 2
        };

        // to refer to this object in inner classes (for example inside event handlers or callbacks)
        var self = this;

        // to discriminate between restart game or play game at the first time
        this.finished = false;

        // a unique id during any set of the game. is restarted after reset or when game get finished.
        this.id = null;

        // to refuse event handlers when drawing is in process
        this.drawing = false;

        // current status
        this.status = STATUS.INIT;

        // the width of large pits (height is calculated by multiplication by 2)
        this.w_large = 120;

        // the radius of pits
        this.radius = 60;

        // the radius of items in pits
        this.pitRasius = 6;

        // width of the board. is set based on the size of the bg.png
        this.width = 1170;

        // margin of elements
        this.margin = 30;

        // current state of the game, not related with this.status. it is a json object like this.initial_state.
        this.current_state = null;

        // initialize width of HTML5 canvas element
        document.getElementById("canvas").width = this.width + 10;

        // the height of the board is same as the height of the canvas
        this.height = document.getElementById("canvas").height;

        // fabfic canvas object
        this.canvas = new fabric.Canvas('canvas');

        // fabric objects which represent pit shapes
        this.pitShapes = {};

        // fabric objects which represent large pit shapes
        this.largePitShapes = {};

        // create a unique key for each pit to recognize the pit when user click on it
        this.pitkey = function(player,pit){
            return player+"_"+pit;
        }

        // lock fabric objects to disable moving or resizing or rotating. it also hides fabric controls arround objects (controls like rotation , scaling , ......)
        this.lock = function(o){
            o.islocked = true;
            o.lockMovementX = true;
            o.lockMovementY = true;
            o.lockScalingX = true;
            o.lockScalingY = true;
            o.lockUniScaling = true;
            o.lockRotation = true;
            o.setControlsVisibility({ml: false, mr: false, mt: false, mb: false, tl: false, tr: false, bl: false, br: false});
        };

        // this function creates a fabric image and call callback when the object is created
        // callback is called when browser fetch image and fabric create an image object
        this.createImage = function(src, left, top, callback){
            fabric.Image.fromURL(src, function(image) {
                image.set({
                    left: left,
                    top: top,
                    strokeWidth : 0,
                    originX:'center',
                    originY:'center'
                });
                // origin of object is center, it means that left and top position of object is calculated based on the position of its center, not the position of its 
                // left and top corner
                image.setCoords();
                image.dirty = true;
                callback(image);
            });
        };

        // draw board and elements like pit , large pit. is called in initialization and when reset is clicked or start button is clicked after finish.
        this.drawBoard = function(callback){
            console.log("drawBoard");
            // get the last state elements of current state
            // note that current state contains a list of states to enable visualization, and drawing board dont need visalization. it only needs the last state.
            var state = this.current_state.states[this.current_state.states.length - 1];

            // to maintain list of objects that represent coordination and other properties of pits and large pits
            var large_pit_shapes = [] , pit_shapes = [];

            // large pit of player2
            var large_pit = state.player2.largePit;
            var left = this.width - this.w_large / 2;
            var top = this.height / 2;
            large_pit_shapes.push({left: left, top: top, player: "player2"});
            
            // large pit of player1
            large_pit = state.player1.largePit;
            left = this.w_large / 2 + this.margin;
            large_pit_shapes.push({left: left, top: top, player: "player1"});

            // pits of player1
            var pits = state.player1.pit , pit;
            top = this.height / 2 - this.w_large / 2;
            left = this.width - this.w_large - this.radius - 10;

            // width that is assigned for each pit
            var d = (left - this.w_large + this.margin + 20) / 6;

            for(pit in pits){
                pit_shapes.push({left: left, top: top, player: "player1", index: pit});
                left = left - d;
            }
            
            // pits of player2
            pits = state.player2.pit;
            top = this.height / 2 + this.w_large / 2;
            left = left + d;
            for(pit in pits){
                pit_shapes.push({left: left, top: top, player: "player2", index: pit});
                left = left + d;
            }

            // this function is called for each large pit recursively and call finish callback after finishing them
            var iterate_large_pit_shapes = function(i, finish){
                if(i >= large_pit_shapes.length){
                    finish();
                    return;
                }
                var shape = large_pit_shapes[i];
                self.createImage("large_pit.png", shape.left, shape.top, function(image){
                    // large pit shapes are not selectable
                    image.hasBorders = false;
                    image.selectable = false;
                    image.opacity = 1;
                    // resize object to fit with w_large
                    image.scaleX = self.w_large / (image.width * image.scaleX);
                    image.scaleY = (self.w_large / image.width *  image.scaleY);
                    self.lock(image);
                    self.canvas.add(image);
                    self.largePitShapes[shape.player] = image;
                    iterate_large_pit_shapes(i+1, finish);
                });
            };

            // this function is called for each pit recursively and call finish callback after finishing them
            var iterate_pit_shapes = function(i, finish){
                if(i >= pit_shapes.length){
                    finish();
                    return;
                }
                var shape = pit_shapes[i];
                self.createImage("pit.png", shape.left, shape.top, function(image){
                    // set the key property to enable finding the relevant properties when user click on it
                    image.key = self.pitkey(shape.player, shape.index);
                    // set opacity to enable users to view small items in pit when the pit is selected (clicked)
                    image.opacity = 0.8;
                    // resize object to fit with radius
                    image.scaleX = (self.radius * 2) / (image.width * image.scaleX);
                    image.scaleY = (self.radius * 2 / image.width *  image.scaleY);
                    self.lock(image);
                    self.canvas.add(image);
                    self.pitShapes[self.pitkey(shape.player,shape.index)] = image;
                    iterate_pit_shapes(i+1, finish);
                });
            };

            iterate_large_pit_shapes(0, function(){
                // draw pit shapes when drawing of large pit shapes get finished
                iterate_pit_shapes(0, function(){
                    self.canvas.renderAll();
                    if(callback){
                        callback();
                    }
                })
            });
        };

        // this function is called when a new state is received from server. is called in reset and also when a new state received from server.
        this.redrawBoardItems = function(callback){
            console.log("redrawBoardItems");
            if(!this.current_state || !this.current_state.states){
                console.log("redrawBoardItems : no current_state");
                return;
            }
            var states = this.current_state.states , i , state;
            var l = states.length;
            // this function is called recursively for each item in current states
            var iterate = function(i){
                if(i >= l){
                    if(callback){
                        callback();
                    }
                    return;
                }
                console.log("redrawBoardItems : state = "+i);
                state = states[i];
                self.playSound();
                self.redrawBoardItemsOfState(state , function(){
                    setTimeout(function(){
                        // call to redraw next state item after 100ms
                        iterate(i + 1);
                    } , 100);
                });
            };
            iterate(0);
        };

        // this function is called by this.redrawBoardItems to redraw items for each state in current states
        this.redrawBoardItemsOfState = function(state, callback){
            // clear previous items
            this.canvas.getObjects().forEach(function(o){
                if(o.temp){
                    self.canvas.remove(o);
                }
            });
            var players = ["player1", "player2"];
            var fills = {"player1" : "#0000FF" , "player2" : "#00FF00"};
            var pits , i , num , j , shape , cont , left , top , dx , dy , left_1 , k , player , fill , key;
            var pit_shapes = [] , large_pit_shapes = [];

            // to maintain the number of items which could be drawn in each row
            var rows = [3, 5, 6, 5, 3] , row , row_split , k;

            // get item color based on the player
            var get_item_fill = function(shape){
                return fills[shape.player];
            };

            // this function set a light shadow for items
            var set_shadow = function(item){
                var shadow = {
                    color: "#000000",
                    blur: 1,
                    offsetX: 3,
                    offsetY: 3,
                };
                item.setShadow(shadow);
            };

            // iterate over players
            for(k in players){
                player = players[k];
                pits = pits = state[player].pit;
                fill = fills[player];
                for(i in pits){
                    num = pits[i];
                    key = this.pitkey(player,i);
                    
                    // get the fabric object of each pit (to calculate the left and top) which is the container of these items
                    cont = this.pitShapes[key];
                    left = cont.left - this.radius + this.radius / 2;
                    top = cont.top - this.radius + 10;

                    // amount of space allocated to each item
                    dx = this.radius * 2 / 6;
                    dy = (this.radius - 10) * 2 / 5;
                    left_1 = left;
                    row = 0;

                    // after how many items it should go to the next row
                    row_split = rows[0];
                    k = 0;

                    // 22 is the maximum number of items which could be drawn in one pit
                    // if the number of items exeeds, add a text to shod the actual number of items
                    if(num > 22){
                        var text = new fabric.IText("...("+num+")", {
                            fontFamily: 'Arial',
                            fontSize: 14,
                            angle: 0,
                            editable: false,
                            fill: fill,
                            fontWeight: 'bold',
                            strokeWidth:0,
                            lineHeight: 1,
                            left: cont.left - 10,
                            top : cont.top + this.radius - 28,
                        });
                        text.temp = true;
                        this.canvas.add(text);
                        num = 20;
                    }
                    for(j = 0 ; j < num ; j++){
                        pit_shapes.push({left:left, top:top, player: player, index: i, i: j, key: key});
                        // go to the next row for following items
                        if((k+1) % row_split == 0){
                            k = 0;
                            top = top + dy;
                            row++;
                            row_split = (row < rows.length ? rows[row] : rows[rows.length-1]);
                            left = left_1 - (row_split - 3) * dx / 2;
                        }
                        else{
                            ++k;
                            left = left + dx;
                        }
                    }
                }
                var large_pit = state[player].largePit;
                // get the fabric object of large pit and its position
                cont = this.largePitShapes[player];
                left = cont.left - this.w_large / 2 + 10;
                top = cont.top - this.w_large + 15;
                dx = (this.w_large - 10) / 5;
                dy = this.w_large / 6;
                left_1 = left;
                for(i = 1; i < large_pit + 1 ; i++){
                    large_pit_shapes.push({left:left, top:top, player: player, i: j});
                    // go to the next row for following items
                    if(i % 5 == 0){
                        left = left_1;
                        top = top + dy;
                    }
                    else{
                        left = left + dx;
                    }
                }
            }

            // this function is called for each item in each large pit to draw
            var iterate_large_pit_items = function(i, finish){
                if(i >= large_pit_shapes.length){
                    finish();
                    return;
                }
                var shape = large_pit_shapes[i];
                // create a small circle for each item in large pit
                var circle = new fabric.Circle({ radius: self.pitRasius, fill: get_item_fill(shape), top: shape.top, left: shape.left });
                // this item is temp and will be cleared in the next redraw
                circle.temp = true;
                circle.hasBorders = false;
                circle.selectable = false;
                set_shadow(circle);
                self.lock(circle);
                circle.opacity = 0.8;
                self.canvas.add(circle);
                iterate_large_pit_items(i+1, finish);
            };

            // this function is called for each item in each pit to draw
            var iterate_pit_items = function(i, finish){
                if(i >= pit_shapes.length){
                    finish();
                    return;
                }
                var shape = pit_shapes[i];
                // create a small circle for each item in pit
                var circle = new fabric.Circle({ radius: self.pitRasius, fill: get_item_fill(shape), top: shape.top, left: shape.left });
                // this item is temp and will be cleared in the next redraw
                circle.temp = true;
                circle.key = shape.key;
                circle.hasBorders = false;
                circle.selectable = false;
                set_shadow(circle);
                self.lock(circle);
                circle.opacity = 0.8;
                self.canvas.add(circle);
                iterate_pit_items(i+1, finish);
            };

            // first draw items in all of the large pits and when it gets finished, draw items in all of the pits
            iterate_large_pit_items(0, function(){
                self.canvas.renderAll();
                iterate_pit_items(0, function(){
                    // render all canvas after finish
                    self.canvas.renderAll();
                    if(callback){
                        callback();
                    }
                });
            })
        };

        // assign a new random id. is called in initialization or when reset button is clicked.
        this.assignID = function(){
            this.id = Math.floor(Math.random() * 10000000);
        };

        // this function is called when the page is loaded at the first time (or when the Mancala class is instantiated)
        this.initialize = function(){
            this.assignID();
            console.log("initialize , id = "+this.id);

            // set the margin-left of board to be in the center of the screen
            document.getElementById("board").style.marginLeft = ((document.getElementById("container").offsetWidth - this.width - 30) / 2) + "px";

            // disable reset button
            document.getElementById("btn_start").disabled = false;
            document.getElementById("btn_reset").disabled = true;

            // click handler for start and reset buttons
            document.getElementById("btn_start").addEventListener("click", function(){
                self.setStatus("Connection to Server ...");
                // if this is clicked after finish
                if(self.finished){
                    console.log("start again , id = "+self.id);
                    self.drawBoard();
                    self.reset(function(){
                        self.request_new_state();
                    });
                }
                else{
                    console.log("start new game , id = "+self.id);
                    self.request_new_state();
                }
            });

            document.getElementById("btn_reset").addEventListener("click", function(){
                self.assignID();
                console.log("reset , id = "+self.id);
                self.drawBoard();
                // call reset and after finishing it, call redrawBoardItems
                self.reset(function(){
                    self.redrawBoardItems();
                });
            });

            // set the current state to initial state
            this.current_state = this.initial_state;

            // draw board and after finishing it, reset
            this.drawBoard(function(){
                self.reset();
            });
        };

        // is called when the current state is changed
        this.on_change_state = function(){
            console.log("on_change_state : state = "+JSON.stringify(this.current_state));
            this.status = this.current_state.status;
            // at start of the game
            if(this.status == STATUS.INIT){
                console.log("on_change_state : init");
                this.setStatus("Select one pit to start game");
            }
            // game is playing
            else if(this.status == STATUS.PLAY){
                console.log("on_change_state : play");
                this.setStatus("Select one pit to continue game");
            }
            // game is finished
            else if(this.status == STATUS.FINISH){
                this.setStatus("Finish");
                this.finished = true;
                document.getElementById("btn_start").disabled = true;
                document.getElementById("btn_reset").disabled = false;
                console.log("on_change_state : finish");
            }
            // redraw items
            this.redrawBoardItems(function(){
                self.on_change_turn();
                // after redraw, object selection is allowed
                self.drawing = false;
            });
        };

        // this function is called when turn of players is changed
        this.on_change_turn = function(){
            if(!this.current_state){
                return;
            }
            var turn = this.current_state.turn , i , shape , selectable;
            console.log("on_change_turn : turn = "+turn);

            // change the selectable property of pits based on the player of each pit which is assigned to it
            for(i in this.pitShapes){
                shape = this.pitShapes[i];
                selectable = i.indexOf(turn+"_")==0;
                shape.selectable = selectable;
            }
            var current , other;
            if(turn=="player1"){
                current = "player1";
                other = "player2";
            }
            else{
                current = "player2";
                other = "player1";
            }

            // change the status of players. the status of current player should have a border which shows its turn.
            this.setPlayerStatus(current , 2);
            this.setPlayerStatus(other , 1);
        };

        // this function is due to change the status text and style of each player
        this.setPlayerStatus = function(player, bold){
            var status = document.getElementById("status_"+player);
            var colors = {"player1" : "#8888FF" , "player2" : "#88FF88"};
            var color = colors[player];

            // calculate the number of items in large pit
            var num;
            if(this.current_state && this.current_state.states){
                num = this.current_state.states[this.current_state.states.length - 1][player].largePit
            }
            if(!num){
                num = 0;
            }
            status.innerHTML = num;
            status.style.color = color;
            if(bold){
                // if bold = 2 then the status text is bold and has a border which shows the turn
                status.style.fontWeight = (bold == 2 ? "bold" : "normal");
                status.style.border = (bold == 2 ? ("5px solid "+color) : "none");
            }
            status.style.fontSize = "20px";
        };

        // events handler of fabric canvas object
        this.canvas.on({
            'mouse:up': function(e){
                // when game is in drawing state, no pit selection is allowed
                if(self.drawing){
                    return;
                }
                // get the selected object. if it has a key it shows that it is a pit or an item in a pit
                var obj = e.target;
                if(!obj || !obj.key){
                    return;
                }
                // get the player and pit number from key
                var split = obj.key.split("_");
                var player = split[0];
                var pit_number = split[1];
                console.log("object selection : player = "+player+" , pit_number = "+pit_number);
                // refuse the player which dont have the right based on current turn
                if(player != self.current_state.turn){
                    console.log("object selection : invalid player");
                    return;
                }
                // start drawing and refuse any other selection until drawing finished
                self.drawing = true;
                self.request_new_state(pit_number);
            },
        });

        // set the status text based on current status
        this.setStatus = function(text){
            document.getElementById("header").innerHTML = text;
        }

        // request new state from server. this function is called when player select a pit or  when start button is clicked.
        this.request_new_state = function(pit_number){
            console.log("request_new_state : pit_number = "+(pit_number ? pit_number : "none"));
            // disable start button
            document.getElementById("btn_start").disabled = true;
            document.getElementById("btn_reset").disabled = false;
            var data = {};
            if(this.current_state && this.current_state.turn){
                data['player'] = this.current_state.turn;
            }
            else{
                // default value for player
                data['player'] = 'player1';
            }
            // pit_number is optional. pit_number is provided when user click on a pit.
            if(pit_number){
                data['pit_number'] = pit_number;
            }
            data['id'] = this.id;
            console.log("request_new_state : request = "+JSON.stringify(data));
            // send an ajax post request
            var xmlhttp = new XMLHttpRequest();
            var url = "/api/game";
            xmlhttp.onreadystatechange = function() {
                console.log("request_new_state : response status = "+this.status+" , readyState = "+this.readyState);
                if (this.readyState == 4 && this.status == 202) {
                    // set current state
                    var new_state = JSON.parse(this.responseText);
                    self.current_state = new_state;
                    self.on_change_state();
                }
            };
            xmlhttp.open("POST", url, true);
            xmlhttp.setRequestHeader('Content-Type', 'application/json');
            xmlhttp.responseType = "application/json";
            xmlhttp.setRequestHeader("Access-Control-Allow-Headers", "Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
            xmlhttp.setRequestHeader("Access-Control-Allow-Methods", "POST");
            xmlhttp.setRequestHeader("Access-Control-Allow-Origin", "*");
            xmlhttp.send(JSON.stringify(data));
        }

        // reset the game. this function is called when reset button is clicked, or when page is loaded, or after finishing game the start button is clicked.
        this.reset = function(callback){
            self.setStatus("Connection to Server ...");
            this.finished = false;
            this.drawing = false;
            this.setPlayerStatus("player1");
            this.setPlayerStatus("player2");
            // set current state to initial state
            this.current_state = this.initial_state;
            console.log("reset : id = "+this.id);
            // send ajax request to restart current state
            var data = {};
            data['command'] = 'restart';
            data['id'] = this.id;
            var xmlhttp = new XMLHttpRequest();
            var url = "/api/game";
            xmlhttp.onreadystatechange = function() {
                console.log("reset : response status = "+this.status+" , readyState = "+this.readyState);
                if (this.readyState == 4 && (this.status == 202 || this.status == 200 || this.status == 201)) {
                    document.getElementById("btn_start").disabled = false;
                    document.getElementById("btn_reset").disabled = true;
                    self.resetStatus();
                    if(callback){
                        callback();
                    }
                }
            };
            xmlhttp.open("PUT", url, true);
            xmlhttp.setRequestHeader('Content-Type', 'application/json');
            xmlhttp.responseType = "application/json";
            xmlhttp.setRequestHeader("Access-Control-Allow-Headers", "Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
            xmlhttp.setRequestHeader("Access-Control-Allow-Methods", "PUT");
            xmlhttp.setRequestHeader("Access-Control-Allow-Origin", "*");
            console.log("reset : request = "+JSON.stringify(data));
            xmlhttp.send(JSON.stringify(data));
        };

        // reset status labels
        this.resetStatus = function(){
            this.setPlayerStatus("player1");
            this.setPlayerStatus("player2");
            this.setStatus("&nbsp;");
        };

        // play sound in every redraw of state
        this.playSound = function() {
            new Audio('sound.wav').play();
        };

        this.initialize();

    };

    var mancala = new Mancala();

})();