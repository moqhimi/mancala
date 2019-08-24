package com.bol.game.mancala.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Request {
	public Request(int id, String player, String pitNumber) {
		this.id = id;
		this.player = player;
		this.pitNumber = pitNumber;
	}

	@JsonProperty("id")
	private int id;
	@JsonProperty("player")
	private String player;
	@JsonProperty("pit_number")
	private String pitNumber;



	public Request() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPlayer() {
		return player;
	}

	public String getPitNumber() {
		return pitNumber;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public void setPitNumber(String pitNumber) {
		this.pitNumber = pitNumber;
	}

	@Override public String toString() {
		return "Request{" + "player='" + player + '\'' + ", pitNumber=" + pitNumber + '}';
	}
}
