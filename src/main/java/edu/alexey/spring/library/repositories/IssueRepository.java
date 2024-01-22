package edu.alexey.spring.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.alexey.spring.library.entities.Issue;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

}
