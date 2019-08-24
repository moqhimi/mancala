package com.bol.game.mancala.exception;

public class IncorrectCommandException  extends RuntimeException {
	public IncorrectCommandException() {
		super();
	}
	public IncorrectCommandException(final String message, final Throwable cause) {
		super(message, cause);
	}
	public IncorrectCommandException(final String message) {
		super(message);
	}
	public IncorrectCommandException(final Throwable cause) {
		super(cause);
	}

}
