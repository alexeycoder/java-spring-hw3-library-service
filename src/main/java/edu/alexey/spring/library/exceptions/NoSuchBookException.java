package edu.alexey.spring.library.exceptions;

import java.util.NoSuchElementException;

public class NoSuchBookException extends NoSuchElementException {

	private static final long serialVersionUID = 1L;

	public NoSuchBookException(long bookId) {
		super("Нет книги с таким ключём \"" + bookId + "\"");
	}
}
