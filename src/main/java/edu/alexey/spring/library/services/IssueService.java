package edu.alexey.spring.library.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import edu.alexey.spring.library.api.IssueRequest;
import edu.alexey.spring.library.entities.Book;
import edu.alexey.spring.library.entities.Issue;
import edu.alexey.spring.library.entities.Reader;
import edu.alexey.spring.library.exceptions.AlreadyCoveredIssueException;
import edu.alexey.spring.library.exceptions.NoSuchBookException;
import edu.alexey.spring.library.exceptions.NoSuchIssueException;
import edu.alexey.spring.library.exceptions.NoSuchReaderException;
import edu.alexey.spring.library.repositories.BookRepository;
import edu.alexey.spring.library.repositories.IssueRepository;
import edu.alexey.spring.library.repositories.ReaderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IssueService {

	private final IssueRepository issueRepository;

	private final ReaderRepository readerRepository;
	private final BookRepository bookRepository;

	public Optional<Issue> findById(long issueId) {
		return issueRepository.findById(issueId);
	}

	public Issue getById(long issueId) {
		return findById(issueId).orElseThrow(() -> new NoSuchIssueException(issueId));
	}

	public long countHeldBooksByReaderId(long readerId) {
		if (!readerRepository.existsById(readerId)) {
			throw new NoSuchReaderException(readerId);
		}

		var matcher = ExampleMatcher.matchingAll().withIgnorePaths("issueId", "bookId", "issuedAt")
				.withIncludeNullValues();
		return issueRepository.count(Example.of(new Issue(null, 0, readerId, null, null), matcher));

		//		var matcher = ExampleMatcher.matchingAll()
		//				.withMatcher("name", GenericPropertyMatchers.contains().ignoreCase())
		//				.withIgnorePaths("groupId");
		//		return studentRepository.findAll(Example.of(new Student(null, namePattern, null), matcher));
	}

	public Issue issue(IssueRequest issueRequest) {

		if (!bookRepository.existsById(issueRequest.getBookId())) {
			throw new NoSuchBookException(issueRequest.getBookId());
		}
		if (!readerRepository.existsById(issueRequest.getReaderId())) {
			throw new NoSuchReaderException(issueRequest.getReaderId());
		}

		var issue = new Issue(null, issueRequest.getBookId(), issueRequest.getReaderId(), LocalDateTime.now(), null);
		return issueRepository.save(issue);
	}

	public Issue cover(long issueId) {
		Issue issue = getById(issueId);
		if (issue.getReturnedAt() != null) {
			throw new AlreadyCoveredIssueException(issueId, issue.getReturnedAt());
		}
		issue.setReturnedAt(LocalDateTime.now());
		return issueRepository.saveAndFlush(issue);
	}

	public IssueDescription getDescriptionById(long issueId) {

		Issue issue = findById(issueId).orElseThrow(() -> new NoSuchIssueException(issueId));
		Book book = bookRepository.findById(issue.getBookId())
				.orElseThrow(() -> new NoSuchBookException(issue.getBookId()));
		Reader reader = readerRepository.findById(issue.getReaderId())
				.orElseThrow(() -> new NoSuchReaderException(issue.getReaderId()));
		return new IssueDescription(issue, book, reader);
	}

}
