package edu.alexey.spring.library.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import edu.alexey.spring.library.entities.User;
import edu.alexey.spring.library.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public Optional<User> findByUsername(String username) {
		Optional<User> userOpt = userRepository.findByUsername(username);
		userOpt.ifPresent(User::getRoles);
		return userOpt;
	}

	//	// В целях упрощения каждой роли соответствует одно одноимённое право: "роль" <=> "право": 
	//	public static final String AUTHORITY_ADMIN = Role.ADMIN;
	//	public static final String AUTHORITY_READER = Role.READER;
	//
	//	private final Map<String, Set<? extends GrantedAuthority>> ROLE_AUTHORITIES = Map.of(
	//			Role.ADMIN, Set.of(new SimpleGrantedAuthority(AUTHORITY_ADMIN)),
	//			Role.READER, Set.of(new SimpleGrantedAuthority(AUTHORITY_READER)));

	//	public Set<? extends GrantedAuthority> getUserAuthorities(User user) {
	//		return user.getRoles().stream()
	//				.map(Role::getRoleName)
	//				.map(ROLE_AUTHORITIES::get)
	//				.flatMap(Set::stream)
	//				.collect(Collectors.toSet());
	//	}
}
