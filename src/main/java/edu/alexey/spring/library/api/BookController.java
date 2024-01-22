package edu.alexey.spring.library.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.alexey.spring.library.entities.Book;
import edu.alexey.spring.library.services.BookService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/book")
public class BookController {

	private final BookService bookService;

	BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping(value = { "", "/", "s" })
	List<Book> all() {
		return bookService.getAll();
	}

	@GetMapping("/{id}")
	ResponseEntity<Book> one(@PathVariable("id") long bookId) {
		return ResponseEntity.ok(bookService.getById(bookId));
	}

	@DeleteMapping("/{id}")
	ResponseEntity<Book> delete(@PathVariable("id") long bookId) {
		return ResponseEntity.ok(bookService.deleteById(bookId));
	}

	@PostMapping()
	ResponseEntity<Book> addNew(@RequestBody Book book) {
		log.info("Запрос на добавление книги \"{}\"", book);

		Book addedEntry = bookService.addNew(book);
		return ResponseEntity
				.created(UriComponentsBuilder.fromPath("/book/{id}").buildAndExpand(addedEntry.getBookId())
						.toUri())
				.body(addedEntry);
	}

}
