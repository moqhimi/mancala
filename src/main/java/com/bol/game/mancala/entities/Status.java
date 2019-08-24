package com.bol.game.mancala.entities;

import java.util.List;

public class Status {
	private List<State> states;
	private int status;
	private String turn;

	public Status(List<State> states, int status, String turn) {
		this.states = states;
		this.status = status;
		this.turn = turn;
	}

	public Status() {
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTurn() {
		return turn;
	}

	public void setTurn(String turn) {
		this.turn = turn;
	}

	public List<State> getStates() {
		return states;
	}

	public void setStates(List<State> states) {
		this.states = states;
	}

	@Override public String toString() {
		return "Status{" + "states=" + states + ", status=" + status + ", turn='" + turn + '\'' + '}';
	}
}
