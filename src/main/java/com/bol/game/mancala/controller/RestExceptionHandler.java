package com.bol.game.mancala.controller;

import com.bol.game.mancala.exception.IncorrectCommandException;
import com.bol.game.mancala.exception.IncorrectPitNumberException;
import com.bol.game.mancala.exception.PlayerNotActiveException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * This class is used to override spring exception handling for our custom exceptions
 *  need to be revised later
 */

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	public RestExceptionHandler() {
		super();
	}

	@ExceptionHandler(IncorrectCommandException.class)
	protected ResponseEntity<Object> handleIncorrectCommand(Exception ex, WebRequest request) {
		return handleExceptionInternal(ex, "Incorrect Command", new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(PlayerNotActiveException.class)
	protected ResponseEntity<Object> handlePlayerNotActive(Exception ex, WebRequest request) {
		return handleExceptionInternal(ex, "Incorrect Player", new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED, request);
	}

	@ExceptionHandler(IncorrectPitNumberException.class)
	protected ResponseEntity<Object> handleIncorrectPitNo(Exception ex, WebRequest request) {
		return handleExceptionInternal(ex, "Incorrect Pit No.", new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler({
			ConstraintViolationException.class,
			DataIntegrityViolationException.class,
	})
	public ResponseEntity<Object> handleBadRequest(Exception ex, WebRequest request) {
		return handleExceptionInternal(ex, ex
				.getLocalizedMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}




}