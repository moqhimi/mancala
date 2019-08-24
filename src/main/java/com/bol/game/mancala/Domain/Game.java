package com.bol.game.mancala.Domain;

import com.bol.game.mancala.entities.Enums.BolcomEnums;
import com.bol.game.mancala.entities.Pit;
import com.bol.game.mancala.entities.Player;
import com.bol.game.mancala.entities.State;
import com.bol.game.mancala.entities.Status;
import com.bol.game.mancala.exception.IncorrectPitNumberException;
import com.bol.game.mancala.exception.PlayerNotActiveException;
import com.bol.game.mancala.service.MancalaService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Game {
	private final static Logger logger = Logger.getLogger(MancalaService.class);

	private int NUMBER_OF_PLAYERS = 2;
	private int BOARD_LENGTH = 6;
	private int NUMBER_OF_STONES = 6;

	private int[][] pits;
	private int[] largePits;
	private int currentPlayer;
	private boolean isBonusTurn;
	private boolean isFinished;

	public void setPits(int[][] pits) {
		this.pits = pits;
	}

	public int[] getLargePits() {
		return largePits;
	}

	public void setLargePits(int[] largePits) {
		this.largePits = largePits;
	}

	/**
	 * Initialize game
	 */
	public Game() {
		logger.debug("new game instantiated");
		pits = new int[NUMBER_OF_PLAYERS][BOARD_LENGTH];
		largePits = new int[NUMBER_OF_PLAYERS];
		isFinished = false;
		for (int p = 0; p < NUMBER_OF_PLAYERS; p++) {
			largePits[p] = 0;
			for (int col = 0; col < BOARD_LENGTH; col++)
				pits[p][col] = NUMBER_OF_STONES;
		}
		currentPlayer = 0;
		isBonusTurn = false;
	}

	public int[][] getPits() {
		return pits.clone();
	}

	/**
	 * Build State object
	 * @return  State
	 */
	public State buildState() {
		return new State(buildPlayer(0), buildPlayer(1));
	}

	/**
	 * Build Player object for a player from pits  and largePit array
	 * @param side player (palyer1-0, palyer2-1)
	 * @return Player
	 */
	public Player buildPlayer(int side) {
		return new Player(buildPit(side), largePits[side]);
	}
	/**
	 * Build pit object for a player from pits array
	 * @param side player (palyer1-0, palyer2-1)
	 * @return  PIt
	 */
	public Pit buildPit(int side) {
		return new Pit(pits[side][0], pits[side][1], pits[side][2], pits[side][3], pits[side][4], pits[side][5]);
	}
	/**
	 * generate init game status
	 * @param side player (palyer1-0, palyer2-1)
	 * @return  Status
	 */
	public Status init(int side) {
		logger.debug("generate new game status");
		List<State> states = new ArrayList<>();
		states.add(buildState());
		return new Status(states, BolcomEnums.GameStatus.START.getValue(), GameUtils.getTurn(side));
	}
	/**
	 * make a move
	 * @param side player (palyer1-0, palyer2-1)
	 * @Param pit which pit has been clicked
	 * @return  Status
	 */
	public Status makeAMove(int side, int pit) {
		if(pit<0 || pit >5){
			throw new IncorrectPitNumberException("Incorrect pit number.");
		}
		logger.debug("makeAMove method->side: "+side+"  pit: "+pit);
		List<State> states = new ArrayList<>();
		if (side != currentPlayer && !isFinished) {
			throw new PlayerNotActiveException("Player not currently active.");
		}
		if (pits[side][pit] == 0) {
			states.add(buildState());
			return new Status(states, BolcomEnums.GameStatus.CONTINUE.getValue(), GameUtils.getTurn(side));
		}
		if (isBonusTurn) {
			isBonusTurn = false;
		}
		int numberOfSows = pits[side][pit];
		int previousPit = pit;
		int previousSide = side;
		while (numberOfSows > 0) {
			pit = getNextPit(pit);
			if (pit == 0) {
				if (side == currentPlayer) {
					++largePits[side];
					--pits[previousSide][previousPit];
					--numberOfSows;
					states.add(buildState());
					if (numberOfSows <= 0) {
						checkIfPitsAreEmpty();
						isBonusTurn = true;
						return new Status(states, BolcomEnums.GameStatus.CONTINUE.getValue(), GameUtils.getTurn(previousSide));
					}
				}
				side = getNextSide(side);
			}
			++pits[side][pit];
			--pits[previousSide][previousPit];
			states.add(buildState());
			--numberOfSows;
		}
		return endTurn(side, pit, states);
	}

	private int getNextPit(int pit) {
		return ++pit%BOARD_LENGTH;
	}

	private int getNextSide(int side) {
		return ++side%NUMBER_OF_PLAYERS;
	}
	/**
	 * Check if the all pits for one side is empty, then end game
	 * @return  boolean- is game finished or not
	 */
	private boolean checkIfPitsAreEmpty() {
		int[][] checkPits = getPits();
		int empty;
		for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
			empty = 0;
			for (int j = 0; j < BOARD_LENGTH; j++) {
				if (checkPits[i][j] == 0)
					empty++;
			}

			if (empty == BOARD_LENGTH) {
				endGame(getNextSide(i));
				return true;
			}
		}
		return false;
	}


	/**
	 * Check if the last stone that a player drops is in his own empty pit.
	 * If so, the player will take that stone and all of his opponent's
	 * adjacent stones and put them in his large pit. Also, checks if one
	 * side of the board is empty
	 *
	 * @param side which player
	 * @param pit  a pit
	 * @param states list of states
	 * @return  Status
	 */
	private Status endTurn(int side, int pit, List<State> states) {
		logger.debug("turn ended");
		if (side == currentPlayer && pits[side][pit] == 1) {
			largePits[side] += 1 + pits[getNextSide(side)][BOARD_LENGTH - pit - 1];
			pits[side][pit] = 0;
			pits[getNextSide(side)][BOARD_LENGTH - pit - 1] = 0;
			states.add(buildState());
			isBonusTurn = true;
		} else {
			currentPlayer = getNextSide(currentPlayer);
		}
		boolean isEnded = checkIfPitsAreEmpty();
		return new Status(states, isEnded ? BolcomEnums.GameStatus.END.getValue() : BolcomEnums.GameStatus.CONTINUE.getValue(), GameUtils.getTurn(currentPlayer));
	}


	/**
	 * Method Used when game would be finished to empty reminder of the board
	 * @param side which player
	 */
	private void endGame(int side) {
		isFinished = true;
		for (int i = 0; i < BOARD_LENGTH; i++) {
			largePits[side] += pits[side][i];
			pits[side][i] = 0;
		}
		if (largePits[currentPlayer] == largePits[getNextSide(currentPlayer)])
			currentPlayer = -1;
		else if (largePits[currentPlayer] < largePits[getNextSide(currentPlayer)])
			currentPlayer = getNextSide(currentPlayer);
	}



}
