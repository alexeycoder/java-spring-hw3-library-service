package edu.alexey.spring.library.security;

import java.util.Arrays;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.alexey.spring.library.entities.Role;
import edu.alexey.spring.library.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserService userService;

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		var user = userService.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));

		String[] roles = user.getRoles().stream()
				.map(Role::getRoleName)
				.toArray(String[]::new);

		log.info("User '{}' has Roles '{}'", user.toString(), Arrays.toString(roles));

		return User.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.roles(roles)
				.build();
	}
}
