package edu.alexey.spring.library.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import edu.alexey.spring.library.entities.Book;
import edu.alexey.spring.library.entities.Issue;
import edu.alexey.spring.library.entities.Reader;
import edu.alexey.spring.library.exceptions.NoSuchReaderException;
import edu.alexey.spring.library.repositories.BookRepository;
import edu.alexey.spring.library.repositories.IssueRepository;
import edu.alexey.spring.library.repositories.ReaderRepository;

@Service
public class ReaderService {

	private final ReaderRepository readerRepository;
	private final IssueRepository issueRepository;
	private final BookRepository bookRepository;

	public ReaderService(
			ReaderRepository readerRepository,
			IssueRepository issueRepository,
			BookRepository bookRepository) {
		this.readerRepository = readerRepository;
		this.issueRepository = issueRepository;
		this.bookRepository = bookRepository;
	}

	public List<Reader> getAll() {
		return readerRepository.findAll();
	}

	public Optional<Reader> findById(long readerId) {
		return readerRepository.findById(readerId);
	}

	public Reader getById(long readerId) {
		return findById(readerId).orElseThrow(() -> new NoSuchReaderException(readerId));
	}

	public Reader deleteById(long readerId) {

		Reader reader = findById(readerId).orElseThrow(() -> new NoSuchReaderException(readerId));
		readerRepository.delete(reader);
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
		return readerRepository.saveAndFlush(reader);
	}

	public List<Issue> findAllIssuesByReaderId(long readerId) {
		var matcher = ExampleMatcher.matchingAll().withIgnorePaths("issueId", "bookId", "issuedAt", "returnedAt");
		return issueRepository.findAll(Example.of(new Issue(null, 0, readerId, null, null), matcher));
	}

	public List<Issue> findUncoveredIssuesByReaderId(long readerId) {
		var matcher = ExampleMatcher.matchingAll().withIgnorePaths("issueId", "bookId", "issuedAt")
				.withIncludeNullValues();
		return issueRepository.findAll(Example.of(new Issue(null, 0, readerId, null, null), matcher));
	}

	public List<Book> getBooksHeldByReaderId(long readerId) {
		return findUncoveredIssuesByReaderId(readerId).stream()
				.mapToLong(Issue::getBookId)
				.mapToObj(bookRepository::getReferenceById)
				.toList();

		//		List<IssueDescription> issueDescriptions = findUncoveredIssuesByReaderId(readerId).stream()
		//				.map(issue -> new IssueDescription(issue, bookRepository.getReferenceById(issue.getBookId()), reader))
		//				.toList();
		//		return issueDescriptions;
	}

}
