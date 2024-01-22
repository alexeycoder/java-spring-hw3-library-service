package edu.alexey.spring.library.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.alexey.spring.library.entities.Issue;
import edu.alexey.spring.library.services.IssueDescription;
import edu.alexey.spring.library.services.IssueService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/issue")
public class IssueController {

	@Value("${application.max-allowed-books:1}")
	private int maxAllowedBooks;

	private final IssueService issueService;

	@PostConstruct
	void postConstr() {
		log.info("application.max-allowed-books = {}", maxAllowedBooks);
	}

	@GetMapping("/{id}")
	public ResponseEntity<IssueDescription> info(@PathVariable("id") long issueId) {
		return ResponseEntity.ok(issueService.getDescriptionById(issueId));
	}

	@GetMapping("/held/{readerId}")
	public ResponseEntity<Long> heldByReader(@PathVariable long readerId) {
		return ResponseEntity.ok(issueService.countHeldBooksByReaderId(readerId));
	}

	@PostMapping()
	ResponseEntity<Issue> issue(@RequestBody IssueRequest request) {
		log.info("Получен запрос на выдачу: readerId = {}, bookId = {}", request.getReaderId(), request.getBookId());
		if (maxAllowedBooks <= 0) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}

		long openIssuesCount = issueService.countHeldBooksByReaderId(request.getReaderId());
		log.info("У читателя на руках имеется {} книг", openIssuesCount);
		if (openIssuesCount >= maxAllowedBooks) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}

		var entry = issueService.issue(request);
		return ResponseEntity
				.created(UriComponentsBuilder.fromPath("/issue/{issueId}")
						.buildAndExpand(entry.getIssueId())
						.toUri())
				.body(entry);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Issue> cover(@PathVariable("id") long issueId) {
		log.info("Получен запрос на возврат: issueId = {}", issueId);

		return ResponseEntity.ok(issueService.cover(issueId));
	}

}
