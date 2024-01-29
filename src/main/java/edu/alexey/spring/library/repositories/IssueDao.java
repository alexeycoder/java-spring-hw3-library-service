package edu.alexey.spring.library.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import edu.alexey.spring.library.entities.Issue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class IssueDao implements BasicRepository<Issue> {

	static final String SELECT_BASE_SQL = "SELECT * FROM issues ";
	static final String SELECT_FAST_SQL = "SELECT TOP 1 1 FROM issues WHERE issue_id = ?";
	static final String INSERT_SQL = "INSERT INTO issues"
			+ "(book_id, reader_id, issued_at, returned_at) "
			+ "VALUE (:bookId, :readerId, :issuedAt, :returnedAt)";
	static final String UPDATE_SQL = "UPDATE issues SET "
			+ "book_id = :bookId, reader_id = :readerId, issued_at = :issuedAt, returned_at = :returnedAt "
			+ "WHERE issue_id = :issueId";
	static final String DELETE_SQL = "DELETE FROM issues WHERE issue_id = ?";

	private final JdbcClient jdbcClient;

	@Override
	public List<Issue> findAll() {
		return jdbcClient.sql(SELECT_BASE_SQL)
				.query(Issue.class)
				.list();
	}

	@Override
	public Optional<Issue> findById(long issueId) {
		return jdbcClient.sql(SELECT_BASE_SQL + " WHERE issue_id = :issueId")
				.param("issueId", issueId)
				.query(Issue.class)
				.optional();
	}

	@Override
	public Issue save(Issue entry) {
		if (entry.getIssuedAt() == null) {
			throw new IllegalArgumentException("entry", new NullPointerException("entry.issuedAt"));
		}

		if (entry.getIssueId() == null || findById(entry.getIssueId()).isEmpty()) {
			var keyHolder = new GeneratedKeyHolder();
			jdbcClient.sql(INSERT_SQL)
					.param("bookId", entry.getBookId())
					.param("readerId", entry.getReaderId())
					.param("issuedAt", entry.getIssuedAt())
					.param("returnedAt", entry.getReturnedAt())
					.update(keyHolder);
			Long savedEntryId = keyHolder.getKey().longValue();
			entry.setReaderId(savedEntryId);
			return entry;
		}

		int affected = jdbcClient.sql(UPDATE_SQL)
				.param("bookId", entry.getBookId())
				.param("readerId", entry.getReaderId())
				.param("issuedAt", entry.getIssuedAt())
				.param("returnedAt", entry.getReturnedAt())
				.param("issueId", entry.getIssueId())
				.update();

		if (affected <= 0) {
			throw new RuntimeException();
		}
		return entry;
	}

	@Override
	public void delete(Issue entry) {
		if (entry.getIssueId() == null) {
			throw new IllegalArgumentException("entry", new NullPointerException("entry.issueId"));
		}
		jdbcClient.sql(DELETE_SQL).param(entry.getIssueId()).update();
	}

	@Override
	public boolean existsById(long issueId) {
		return jdbcClient.sql(SELECT_FAST_SQL).param(issueId).query((ResultSetExtractor<Boolean>) (rse -> rse.next()));
	}

	public long count(Example<Issue> of) {
		throw new UnsupportedOperationException();
	}

	public List<Issue> findAllByReaderId(long readerId) {
		return jdbcClient.sql(SELECT_BASE_SQL + "WHERE reader_id = ?")
				.param(readerId)
				.query(Issue.class)
				.list();
	}

	public List<Issue> findUncoveredByReaderId(long readerId) {
		return jdbcClient.sql(SELECT_BASE_SQL + "WHERE reader_id = ? AND returned_at IS NULL")
				.param(readerId)
				.query(Issue.class)
				.list();
	}

}
