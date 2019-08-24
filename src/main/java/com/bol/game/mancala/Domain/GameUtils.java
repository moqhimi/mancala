package com.bol.game.mancala.Domain;

import com.bol.game.mancala.entities.Enums.BolcomEnums;
import com.bol.game.mancala.entities.Status;
import com.bol.game.mancala.exception.PlayerNotActiveException;

public class GameUtils {

	public static String getTurn(int side){

		return BolcomEnums.Player.getName(side);
	}
	public static int getTurnInt(String player ){
		if(BolcomEnums.Player.player2.getName().equalsIgnoreCase(player)){
			return BolcomEnums.Player.player2.getValue();
		}else if(BolcomEnums.Player.player1.getName().equalsIgnoreCase(player)) {
			return BolcomEnums.Player.player1.getValue();
		} else {
			throw new PlayerNotActiveException("Player not found");
		}
	}
	public static Status getStatus(Game game, int side, String pitNo) {
		Status status;
		if(pitNo!=null) {
			pitNo = pitNo.replaceAll("pit", "");
			status = game.makeAMove(side, Integer.parseInt(pitNo)-1);
		}else {
			status = game.init(side);
		}
		return status;
	}
}
