package edu.alexey.spring.library.api;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import edu.alexey.spring.library.exceptions.AlreadyCoveredIssueException;

@RestControllerAdvice
public class CommonExceptionsAdvice {

	//@ResponseBody
	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String noSuchEntryHandler(NoSuchElementException ex) {
		return ex.getMessage();
	}

	//@ResponseBody
	@ExceptionHandler(AlreadyCoveredIssueException.class)
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	String alreadyCoveredIssueHandler(RuntimeException ex) {
		return ex.getMessage();
	}

	//@ResponseBody
	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	String wrongRequestHandler(RuntimeException ex) {
		return ex.getMessage();
	}

}
