package edu.alexey.spring.library.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.alexey.spring.library.aux.Timer;
import edu.alexey.spring.library.entities.Book;
import edu.alexey.spring.library.services.BookService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

@Controller("web.BookController")
@RequiredArgsConstructor
@RequestMapping("/ui")
public class BookController {

	private final BookService bookService;

	@Hidden
	@GetMapping("/justtext")
	@ResponseBody
	String test() {
		return "(тест) просто строка как ответ";
	}

	@Timer
	@GetMapping("/books")
	String books(Model model) {

		List<Book> items = bookService.getAll();
		model.addAttribute("items", items);
		return "books";
	}
}
