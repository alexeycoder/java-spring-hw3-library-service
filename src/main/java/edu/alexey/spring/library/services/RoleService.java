package edu.alexey.spring.library.services;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.alexey.spring.library.entities.Role;
import edu.alexey.spring.library.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

	private final RoleRepository roleRepository;

	public List<Role> findAll() {
		return roleRepository.findAll();
	}
}
