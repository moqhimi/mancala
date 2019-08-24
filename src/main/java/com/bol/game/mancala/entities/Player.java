package com.bol.game.mancala.entities;

public class Player {
	private Pit pit;
	private int largePit;

	public Player() {
	}

	public Player(Pit pit, int largePit) {
		this.pit = pit;
		this.largePit = largePit;
	}

	public Pit getPit() {
		return pit;
	}

	public void setPit(Pit pit) {
		this.pit = pit;
	}

	public int getLargePit() {
		return largePit;
	}

	public void setLargePit(int largePit) {
		this.largePit = largePit;
	}

	@Override public String toString() {
		return "Player{" + "pit=" + pit.toString() + ", largePit=" + largePit + '}';
	}
}
