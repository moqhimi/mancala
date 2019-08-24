package com.bol.game.mancala.service;
import com.bol.game.mancala.Domain.Game;
import org.springframework.stereotype.Service;

@Service
public class GameFactoryService {
	/**
	 * Create Game Instance
	 * @return Game
	 */
	Game createGame() {
		return new Game();
	}
}
