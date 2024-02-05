package edu.alexey.spring.library.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import edu.alexey.spring.library.entities.Role;
import edu.alexey.spring.library.services.UserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		var user = userService.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));

		System.out.println(user.getRoles().toString());

		String[] roles = user.getRoles().stream()
				.map(Role::getRoleName)
				.toArray(String[]::new);

		//		System.out.println(Arrays.toString(roles));

		//		var userAuthorities = userService.getUserAuthorities(user);
		var springSecUser = User.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.roles(roles)
				.build();

		//System.out.println(springSecUser);

		return springSecUser;

	}
}
