package edu.alexey.spring.library.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import edu.alexey.spring.library.entities.Book;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class BookDao implements BasicRepository<Book> {

	static final String SELECT_BASE_SQL = "SELECT book_id, name FROM books ";
	static final String SELECT_FAST_SQL = "SELECT TOP 1 1 FROM books WHERE book_id = ?";
	static final String INSERT_SQL = "INSERT INTO books(name) VALUE (:name)";
	static final String UPDATE_SQL = "UPDATE books SET name = :name WHERE book_id = :bookId";
	static final String DELETE_SQL = "DELETE FROM books WHERE book_id = ?";

	private final JdbcClient jdbcClient;

	@Override
	public List<Book> findAll() {
		return jdbcClient.sql(SELECT_BASE_SQL)
				.query(Book.class)
				.list();
	}

	@Override
	public Optional<Book> findById(long bookId) {
		return jdbcClient.sql(SELECT_BASE_SQL + " WHERE book_id = :bookId")
				.param("bookId", bookId)
				.query(Book.class)
				.optional();
	}

	@Override
	public Book save(Book entry) {
		if (entry.getName() == null || entry.getName().isBlank()) {
			throw new IllegalArgumentException("entry");
		}

		if (entry.getBookId() == null || findById(entry.getBookId()).isEmpty()) {
			var keyHolder = new GeneratedKeyHolder();
			jdbcClient.sql(INSERT_SQL)
					.param("name", entry.getName())
					.update(keyHolder);
			Long savedEntryId = keyHolder.getKey().longValue();
			entry.setBookId(savedEntryId);
			return entry;
		}

		int affected = jdbcClient.sql(UPDATE_SQL)
				.param("name", entry.getName())
				.param("bookId", entry.getBookId())
				.update();

		if (affected <= 0) {
			throw new RuntimeException();
		}
		return entry;
	}

	@Override
	public void delete(Book entry) {
		if (entry.getBookId() == null) {
			throw new IllegalArgumentException("entry");
		}
		jdbcClient.sql(DELETE_SQL).param(entry.getBookId()).update();
	}

	@Override
	public boolean existsById(long bookId) {
		return jdbcClient.sql(SELECT_FAST_SQL).param(bookId).query((ResultSetExtractor<Boolean>) (rse -> rse.next()));
	}
}
