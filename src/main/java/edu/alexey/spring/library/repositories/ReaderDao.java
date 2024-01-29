package edu.alexey.spring.library.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import edu.alexey.spring.library.entities.Reader;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ReaderDao implements BasicRepository<Reader> {

	static final String SELECT_BASE_SQL = "SELECT reader_id, name FROM readers ";
	static final String SELECT_FAST_SQL = "SELECT TOP 1 1 FROM readers WHERE reader_id = ?";
	static final String INSERT_SQL = "INSERT INTO readers(name) VALUE (:name)";
	static final String UPDATE_SQL = "UPDATE readers SET name = :name WHERE reader_id = :readerId";
	static final String DELETE_SQL = "DELETE FROM readers WHERE reader_id = ?";

	private final JdbcClient jdbcClient;

	@Override
	public List<Reader> findAll() {
		return jdbcClient.sql(SELECT_BASE_SQL)
				.query(Reader.class)
				.list();
	}

	@Override
	public Optional<Reader> findById(long readerId) {
		return jdbcClient.sql(SELECT_BASE_SQL + " WHERE reader_id = :readerId")
				.param("readerId", readerId)
				.query(Reader.class)
				.optional();
	}

	@Override
	public Reader save(Reader entry) {
		if (entry.getName() == null || entry.getName().isBlank()) {
			throw new IllegalArgumentException("entry");
		}

		if (entry.getReaderId() == null || findById(entry.getReaderId()).isEmpty()) {
			var keyHolder = new GeneratedKeyHolder();
			jdbcClient.sql(INSERT_SQL)
					.param("name", entry.getName())
					.update(keyHolder);
			Long savedEntryId = keyHolder.getKey().longValue();
			entry.setReaderId(savedEntryId);
			return entry;
		}

		int affected = jdbcClient.sql(UPDATE_SQL)
				.param("name", entry.getName())
				.param("readerId", entry.getReaderId())
				.update();

		if (affected <= 0) {
			throw new RuntimeException();
		}
		return entry;
	}

	@Override
	public void delete(Reader entry) {
		if (entry.getReaderId() == null) {
			throw new IllegalArgumentException("entry");
		}
		jdbcClient.sql(DELETE_SQL).param(entry.getReaderId()).update();
	}

	@Override
	public boolean existsById(long readerId) {
		return jdbcClient.sql(SELECT_FAST_SQL).param(readerId).query((ResultSetExtractor<Boolean>) (rse -> rse.next()));
	}
}
