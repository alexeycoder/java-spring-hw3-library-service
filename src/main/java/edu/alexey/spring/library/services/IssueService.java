package edu.alexey.spring.library.services;

import java.time.LocalDateTime;
import java.util.List;
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
import edu.alexey.spring.library.repositories.BookDao;
import edu.alexey.spring.library.repositories.IssueDao;
import edu.alexey.spring.library.repositories.ReaderDao;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class IssueService {

	private final IssueDao issueRepository;

	private final ReaderDao readerDao;
	private final BookDao bookDao;

	public List<Issue> getAll() {
		return issueRepository.findAll();
	}

	public Optional<Issue> findById(long issueId) {
		return issueRepository.findById(issueId);
	}

	public Issue getById(long issueId) {
		return findById(issueId).orElseThrow(() -> new NoSuchIssueException(issueId));
	}

	public long countHeldBooksByReaderId(long readerId) {
		if (!readerDao.existsById(readerId)) {
			throw new NoSuchReaderException(readerId);
		}

		var matcher = ExampleMatcher.matchingAll().withIgnorePaths("issueId", "bookId", "issuedAt")
				.withIncludeNullValues();
		return issueRepository.count(Example.of(new Issue(null, 0, readerId, null, null), matcher));
	}

	public Issue issue(IssueRequest issueRequest) {

		if (!bookDao.existsById(issueRequest.getBookId())) {
			throw new NoSuchBookException(issueRequest.getBookId());
		}
		if (!readerDao.existsById(issueRequest.getReaderId())) {
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
		return issueRepository.save(issue);
	}

	public IssueDescription getDescriptionById(long issueId) {

		Issue issue = findById(issueId).orElseThrow(() -> new NoSuchIssueException(issueId));
		Book book = bookDao.findById(issue.getBookId())
				.orElseThrow(() -> new NoSuchBookException(issue.getBookId()));
		Reader reader = readerDao.findById(issue.getReaderId())
				.orElseThrow(() -> new NoSuchReaderException(issue.getReaderId()));
		return new IssueDescription(issue, book, reader);
	}

}
