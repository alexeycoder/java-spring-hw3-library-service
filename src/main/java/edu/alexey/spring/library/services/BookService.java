package edu.alexey.spring.library.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import edu.alexey.spring.library.entities.Book;
import edu.alexey.spring.library.exceptions.NoSuchBookException;
import edu.alexey.spring.library.repositories.BookRepository;

@Service
public class BookService {

	private final BookRepository bookRepository;

	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	public List<Book> getAll() {
		return bookRepository.findAll();
	}

	public Optional<Book> findById(long bookId) {
		return bookRepository.findById(bookId);
	}

	public Book getById(long bookId) {
		return findById(bookId).orElseThrow(() -> new NoSuchBookException(bookId));
	}

	public Book deleteById(long bookId) {

		Book book = findById(bookId).orElseThrow(() -> new NoSuchBookException(bookId));
		bookRepository.delete(book);
		return book;
	}

	public Book addNew(Book book) {
		if (book.getName() == null) {
			throw new NullPointerException("Название книги не задано (null)");
		}

		if (book.getName().isBlank()) {
			throw new IllegalArgumentException("Недопустимое название книги");
		}

		book.setBookId(null);
		return bookRepository.saveAndFlush(book);
	}
}
