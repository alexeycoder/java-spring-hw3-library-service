package edu.alexey.spring.library.web;

import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.alexey.spring.library.entities.Book;
import edu.alexey.spring.library.entities.Reader;
import edu.alexey.spring.library.services.ReaderService;
import lombok.RequiredArgsConstructor;

@Controller("web.ReaderController")
@RequiredArgsConstructor
@RequestMapping("/ui")
@Secured("ROLE_READER")
public class ReaderController {

	private final ReaderService readerService;

	@GetMapping("/readers")
	String readers(Model model) {

		List<Reader> items = readerService.getAll();
		model.addAttribute("items", items);
		return "readers";
	}

	@GetMapping("/readers/{id}")
	String heldBooks(@PathVariable("id") long readerId, Model model) {
		Reader reader = readerService.getById(readerId);
		List<Book> heldBooks = readerService.getBooksHeldByReaderId(readerId);

		model.addAttribute("reader", reader);
		model.addAttribute("items", heldBooks);
		return "readers/held_books";
	}
}
