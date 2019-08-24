package com.bol.game.mancala.exception;

public class PlayerNotActiveException extends RuntimeException{
	public PlayerNotActiveException() {
		super();
	}
	public PlayerNotActiveException(final String message, final Throwable cause) {
		super(message, cause);
	}
	public PlayerNotActiveException(final String message) {
		super(message);
	}
	public PlayerNotActiveException(final Throwable cause) {
		super(cause);
	}
}
