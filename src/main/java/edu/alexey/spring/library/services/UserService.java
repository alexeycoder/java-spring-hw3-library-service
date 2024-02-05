package edu.alexey.spring.library.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import edu.alexey.spring.library.entities.User;
import edu.alexey.spring.library.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	//	// В целях упрощения каждой роли соответствует одно одноимённое право: "роль" <=> "право": 
	//	public static final String AUTHORITY_ADMIN = Role.ADMIN;
	//	public static final String AUTHORITY_READER = Role.READER;
	//
	//	private final Map<String, Set<? extends GrantedAuthority>> ROLE_AUTHORITIES = Map.of(
	//			Role.ADMIN, Set.of(new SimpleGrantedAuthority(AUTHORITY_ADMIN)),
	//			Role.READER, Set.of(new SimpleGrantedAuthority(AUTHORITY_READER)));

	private final UserRepository userRepository;

	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	//	public Set<? extends GrantedAuthority> getUserAuthorities(User user) {
	//		return user.getRoles().stream()
	//				.map(Role::getRoleName)
	//				.map(ROLE_AUTHORITIES::get)
	//				.flatMap(Set::stream)
	//				.collect(Collectors.toSet());
	//	}
}
