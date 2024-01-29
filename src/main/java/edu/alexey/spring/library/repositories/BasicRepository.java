package edu.alexey.spring.library.repositories;

import java.util.List;
import java.util.Optional;

public interface BasicRepository<T> {

	List<T> findAll();

	Optional<T> findById(long id);

	T save(T entry);

	void delete(T entry);

	boolean existsById(long id);
}
