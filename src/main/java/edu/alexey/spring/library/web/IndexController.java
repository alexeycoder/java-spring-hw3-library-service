package edu.alexey.spring.library.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller("web.IndexController")
@RequestMapping("/")
@RequiredArgsConstructor
public class IndexController {

	@GetMapping()
	public String index() {
		return "index";
	}

}
