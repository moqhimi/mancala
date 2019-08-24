package com.bol.game.mancala.service;

import com.bol.game.mancala.Domain.Game;
import com.bol.game.mancala.Domain.GameUtils;
import com.bol.game.mancala.entities.CommandRequest;
import com.bol.game.mancala.entities.Enums.BolcomEnums;
import com.bol.game.mancala.entities.Request;
import com.bol.game.mancala.entities.Status;
import com.bol.game.mancala.exception.IncorrectCommandException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class MancalaService {
	private final static Logger logger = Logger.getLogger(MancalaService.class);


	@Autowired
	GameFactoryService gameFactoryService;
	public static HashMap<Integer, Game> games = new HashMap<>();

	/**
	 *  Serves move action from UI and return status
	 * @param request
	 * @return
	 */
	public Status moveAction(Request request) {
		logger.debug("start moveAction with request: "+ request.toString());
		Game game;
		if (!games.containsKey(request.getId())) {
			game = gameFactoryService.createGame();
			games.put(request.getId(), game);
		} else {
			game = games.get(request.getId());
		}
		int side = GameUtils.getTurnInt(request.getPlayer());
		Status status;
		String pitNo = request.getPitNumber();
		status = GameUtils.getStatus(game, side, pitNo);
		logger.debug(" returned status: "+ status.toString());
		return status;
	}

	/**
	 * Serves command Action from UI.. could be extended
	 * @param commandRequest- command
	 * @return
	 */
	public Status commandAction(CommandRequest commandRequest) {
		logger.debug("start commandAction with request: "+ commandRequest.toString());
		if (StringUtils.isNotBlank(commandRequest.getCommand()) && commandRequest.getCommand().equalsIgnoreCase("restart")) {
			Game game = gameFactoryService.createGame();
			return game.init(BolcomEnums.Player.player1.getValue());
		} else {
			logger.error("Illegal command");
			throw new IncorrectCommandException();
		}
	}

}
