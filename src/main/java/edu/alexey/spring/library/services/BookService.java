package edu.alexey.spring.library.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import edu.alexey.spring.library.entities.Book;
import edu.alexey.spring.library.exceptions.NoSuchBookException;
import edu.alexey.spring.library.repositories.BookDao;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BookService {

	private final BookDao bookDao;

	public List<Book> getAll() {
		return bookDao.findAll();
	}

	public Optional<Book> findById(long bookId) {
		return bookDao.findById(bookId);
	}

	public Book getById(long bookId) {
		return findById(bookId).orElseThrow(() -> new NoSuchBookException(bookId));
	}

	public Book deleteById(long bookId) {

		Book book = findById(bookId).orElseThrow(() -> new NoSuchBookException(bookId));
		bookDao.delete(book);
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
		return bookDao.save(book);
	}
}
