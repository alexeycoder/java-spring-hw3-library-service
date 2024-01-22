package edu.alexey.spring.library.exceptions;

import java.util.NoSuchElementException;

public class NoSuchReaderException extends NoSuchElementException {

	private static final long serialVersionUID = 1L;

	public NoSuchReaderException(Long readerId) {
		super("Нет читателя с таким ключём \"" + readerId + "\"");
	}

}
