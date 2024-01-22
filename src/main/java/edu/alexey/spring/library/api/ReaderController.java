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

import edu.alexey.spring.library.entities.Issue;
import edu.alexey.spring.library.entities.Reader;
import edu.alexey.spring.library.services.ReaderService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/reader")
public class ReaderController {

	private final ReaderService readerService;

	ReaderController(ReaderService readerService) {
		this.readerService = readerService;
	}

	@GetMapping(value = { "", "/" })
	List<Reader> all() {
		return readerService.getAll();
	}

	@GetMapping("/{id}")
	ResponseEntity<Reader> one(@PathVariable("id") long readerId) {
		return ResponseEntity.ok(readerService.getById(readerId));
	}

	@DeleteMapping("/{id}")
	ResponseEntity<Reader> delete(@PathVariable("id") long readerId) {
		return ResponseEntity.ok(readerService.deleteById(readerId));
	}

	@PostMapping(value = { "", "/" })
	ResponseEntity<Reader> addNew(@RequestBody Reader reader) {
		log.info("Запрос на добавление книги \"{}\"", reader);

		Reader addedEntry = readerService.addNew(reader);
		return ResponseEntity
				.created(UriComponentsBuilder.fromPath("/reader/{id}").buildAndExpand(addedEntry.getReaderId())
						.toUri())
				.body(addedEntry);
	}

	@GetMapping("/{id}/issue")
	public List<Issue> issuesByReader(@PathVariable("id") long readerId) {
		return readerService.findAllIssuesByReaderId(readerId);
	}

}
