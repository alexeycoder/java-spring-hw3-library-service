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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Book", description = "The Book API")
@RequiredArgsConstructor
@RestController("api.BookController")
@RequestMapping("/books")
public class BookController {

	private final BookService bookService;

	@Operation(summary = "Gets all books")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "All books of the library", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Book.class))) }) })
	@GetMapping()
	List<Book> all() {
		return bookService.getAll();
	}

	// @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content(schema = @Schema(hidden = true)))
	@Operation(summary = "Get book by id", description = "Book must exist")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The book found", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Book not found", content = @Content) })
	@GetMapping("/{id}")
	ResponseEntity<Book> one(@PathVariable("id") long bookId) {
		return ResponseEntity.ok(bookService.getById(bookId));
	}

	@Operation(summary = "Add new book")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "The created book URL in Location field", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid book data supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Book not found", content = @Content) })
	@PostMapping()
	ResponseEntity<Void> addNew(@RequestBody Book book) {
		log.info("Запрос на добавление книги \"{}\"", book);

		Book addedEntry = bookService.addNew(book);
		return ResponseEntity
				.created(UriComponentsBuilder.fromPath("/books/{id}")
						.buildAndExpand(addedEntry.getBookId())
						.toUri())
				.build();
	}

	@Operation(summary = "Delete book by id", description = "Book must exist")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "The book deletion confirmation", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Book not found", content = @Content) })
	@DeleteMapping("/{id}")
	ResponseEntity<Void> delete(@PathVariable("id") long bookId) {
		bookService.deleteById(bookId);
		return ResponseEntity.noContent().build();
	}
}
