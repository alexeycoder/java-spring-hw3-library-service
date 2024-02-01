package edu.alexey.spring.library.api;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import edu.alexey.spring.library.exceptions.AlreadyCoveredIssueException;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestControllerAdvice("api.CommonExceptionsAdvice")
public class CommonExceptionsAdvice {

	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	ResponseEntity<String> noSuchEntryHandler(NoSuchElementException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(AlreadyCoveredIssueException.class)
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	ResponseEntity<String> alreadyCoveredIssueHandler(AlreadyCoveredIssueException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ResponseEntity<String> badRequestHandler(MethodArgumentTypeMismatchException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	ResponseEntity<String> suspiciousRequestHandler(RuntimeException ex) {
		System.out.println(ex.getClass().getSimpleName());
		ex.printStackTrace();
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
	}
}
