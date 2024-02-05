package edu.alexey.spring.library.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import edu.alexey.spring.library.entities.Role;

@Configuration
@EnableWebSecurity
public class SecurityConfigurer {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(requests -> requests
				.requestMatchers("/").permitAll()
				.requestMatchers("/h2-console/**").hasRole(Role.ADMIN)
				.requestMatchers("/ui/issues/**").hasRole(Role.ADMIN)
				.requestMatchers("/ui/readers/**").hasRole(Role.READER)
				.requestMatchers("/ui/**").authenticated()
				.requestMatchers("/books/**", "/readers/**", "/issues/**").permitAll()
				.anyRequest().denyAll())

				.formLogin(login -> login // enable form based log in
						// set permitAll for all URLs associated with Form Login
						.permitAll());

		return http.build();
	}

	//	public static void main(String[] args) {
	//		var sc = new SecurityConfigurer();
	//		System.out.println(sc.passwordEncoder().encode("password"));
	//		System.out.println(sc.passwordEncoder().encode("пароль"));
	//		System.out.println(sc.passwordEncoder().encode("ляляля"));
	//		System.out.println(sc.passwordEncoder().encode("12345"));
	//	}
}
