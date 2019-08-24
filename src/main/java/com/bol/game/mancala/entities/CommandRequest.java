package com.bol.game.mancala.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommandRequest {
	@JsonProperty("command")
	private String command;

	public CommandRequest() {
	}

	public CommandRequest(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

}
