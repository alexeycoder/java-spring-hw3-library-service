package edu.alexey.spring.library.web;

import java.nio.channels.IllegalBlockingModeException;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.alexey.spring.library.aux.RecoverException;
import lombok.RequiredArgsConstructor;

@Controller("web.IndexController")
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexController {

	@GetMapping()
	public String index(Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentPrincipalName = authentication.getName();
			model.addAttribute("username", currentPrincipalName);
		}

		return "index";
	}

	@GetMapping("/some")
	@RecoverException //(noRecoverFor = IllegalStateException.class)
	public float someMethod() {
		throw new IllegalBlockingModeException();
	}

}
