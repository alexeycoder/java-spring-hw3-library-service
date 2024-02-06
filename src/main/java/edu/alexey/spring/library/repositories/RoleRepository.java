package edu.alexey.spring.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.alexey.spring.library.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

}
