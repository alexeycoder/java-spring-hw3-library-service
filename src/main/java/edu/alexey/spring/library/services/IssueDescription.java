package edu.alexey.spring.library.services;

import java.time.LocalDateTime;
import java.util.Objects;

import edu.alexey.spring.library.entities.Book;
import edu.alexey.spring.library.entities.Issue;
import edu.alexey.spring.library.entities.Reader;

public record IssueDescription(
		long issueId,
		LocalDateTime issuedAt,
		LocalDateTime returnedAt,
		Book book,
		Reader reader) {
	public IssueDescription(Issue issue, Book book, Reader reader) {
		this(issue.getIssueId(),
				issue.getIssuedAt(),
				issue.getReturnedAt(),
				book,
				reader);
	}

	public IssueDescription {
		Objects.requireNonNull(book);
		Objects.requireNonNull(reader);
	}
}
