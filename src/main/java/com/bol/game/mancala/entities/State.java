package com.bol.game.mancala.entities;

public class State {
	private Player player1;
	private Player player2;

	public State(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
	}

	public State() {
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	@Override public String toString() {
		return "State{" + "player1=" + player1.toString() + ", player2=" + player2.toString() + '}';
	}
}
