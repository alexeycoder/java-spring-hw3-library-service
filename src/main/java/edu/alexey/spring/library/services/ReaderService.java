package edu.alexey.spring.library.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import edu.alexey.spring.library.entities.Book;
import edu.alexey.spring.library.entities.Issue;
import edu.alexey.spring.library.entities.Reader;
import edu.alexey.spring.library.exceptions.NoSuchReaderException;
import edu.alexey.spring.library.repositories.BookDao;
import edu.alexey.spring.library.repositories.IssueDao;
import edu.alexey.spring.library.repositories.ReaderDao;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReaderService {

	private final ReaderDao readerDao;
	private final IssueDao issueDao;
	private final BookDao bookDao;

	public List<Reader> getAll() {
		return readerDao.findAll();
	}

	public Optional<Reader> findById(long readerId) {
		return readerDao.findById(readerId);
	}

	public Reader getById(long readerId) {
		return findById(readerId).orElseThrow(() -> new NoSuchReaderException(readerId));
	}

	public Reader deleteById(long readerId) {

		Reader reader = findById(readerId).orElseThrow(() -> new NoSuchReaderException(readerId));
		readerDao.delete(reader);
		return reader;
	}

	public Reader addNew(Reader reader) {
		if (reader.getName() == null) {
			throw new NullPointerException("Имя читателя не задано (null)");
		}

		if (reader.getName().isBlank()) {
			throw new IllegalArgumentException("Недопустимое имя читателя");
		}

		reader.setReaderId(null);
		return readerDao.save(reader);
	}

	public List<Issue> findAllIssuesByReaderId(long readerId) {
		return issueDao.findAllByReaderId(readerId);
	}

	public List<Issue> findUncoveredIssuesByReaderId(long readerId) {
		return issueDao.findUncoveredByReaderId(readerId);
	}

	public List<Book> getBooksHeldByReaderId(long readerId) {
		return findUncoveredIssuesByReaderId(readerId).stream()
				.mapToLong(Issue::getBookId)
				.mapToObj(bookDao::findById).map(Optional::get)
				.toList();
	}

}
