package edu.alexey.spring.library.exceptions;

import java.util.NoSuchElementException;

public class NoSuchIssueException extends NoSuchElementException {

	private static final long serialVersionUID = 1L;

	public NoSuchIssueException(long issueId) {
		super("Нет выдачи с таким ключём \"" + issueId + "\"");
	}

}
