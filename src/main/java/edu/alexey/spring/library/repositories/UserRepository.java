package edu.alexey.spring.library.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.alexey.spring.library.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);
}
