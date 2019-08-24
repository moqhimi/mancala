package com.bol.game.mancala.controller;

import com.bol.game.mancala.entities.CommandRequest;
import com.bol.game.mancala.entities.Request;
import com.bol.game.mancala.entities.Status;
import com.bol.game.mancala.service.MancalaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/game")
public class MancalaController {

	@Autowired
	MancalaService mancalaService;

	/**
	 * Process actions received from UI
	 *
	 * @param request - RequestFromUi
	 * @return - Status status of nodes  to be used in UI
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.ACCEPTED)
	public Status processAction( @RequestBody Request request) {
		return mancalaService.moveAction(request);
	}

	/**
	 * Process actions received from UI
	 *
	 * @param commandRequest - Command Request From Ui
	 * @return - Status status of nodes  to be used in UI
	 */
	@PutMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Status processCommand(@RequestBody CommandRequest commandRequest){
		return mancalaService.commandAction(commandRequest);
	}




}
