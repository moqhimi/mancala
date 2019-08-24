package com.bol.game.mancala.exception;

public class IncorrectPitNumberException extends RuntimeException{
	public IncorrectPitNumberException() {
		super();
	}
	public IncorrectPitNumberException(final String message, final Throwable cause) {
		super(message, cause);
	}
	public IncorrectPitNumberException(final String message) {
		super(message);
	}
	public IncorrectPitNumberException(final Throwable cause) {
		super(cause);
	}
}
