package edu.alexey.spring.library.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.alexey.spring.library.entities.Issue;
import edu.alexey.spring.library.services.IssueDescription;
import edu.alexey.spring.library.services.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController("api.IssueController")
@RequestMapping("/issues")
public class IssueController {

	@Value("${application.max-allowed-books:1}")
	private int maxAllowedBooks;

	private final IssueService issueService;

	@PostConstruct
	void postConstr() {
		log.info("application.max-allowed-books = {}", maxAllowedBooks);
	}

	@Operation(summary = "Get issue description by issue id", description = "Issue must exist")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Description of the issue found", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = IssueDescription.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Issue not found", content = @Content) })
	@GetMapping("/{id}")
	public ResponseEntity<IssueDescription> infoById(@PathVariable("id") long issueId) {
		return ResponseEntity.ok(issueService.getDescriptionById(issueId));
	}

	@Operation(summary = "Count books held by reader by reader id", description = "Reader must exist")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The number of books held by reader", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Long.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid reader id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Reader not found", content = @Content) })
	@GetMapping("/held/{readerId}")
	public ResponseEntity<Long> countHeldBooksByReaderId(@PathVariable long readerId) {
		return ResponseEntity.ok(issueService.countHeldBooksByReaderId(readerId));
	}

	@Operation(summary = "Does issue book to reader", description = "Book and reader must exist")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "The carried out issue and its URL in Location field", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Issue.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid book id or reader id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Book or reader not found", content = @Content),
			@ApiResponse(responseCode = "409", description = "Exceeded allowed amount of held books", content = @Content) })
	@PostMapping()
	ResponseEntity<Issue> doIssue(@RequestBody IssueRequest request) {
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
				.created(UriComponentsBuilder.fromPath("/issues/{issueId}")
						.buildAndExpand(entry.getIssueId())
						.toUri())
				.body(entry);
	}

	@Operation(summary = "Covers issue i.e. returns the issued book to library", description = "Issue must exist")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The covered issue", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Issue.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Issue not found", content = @Content),
			@ApiResponse(responseCode = "412", description = "Already covered issue", content = @Content) })
	@RequestMapping(path = "/{id}", method = { RequestMethod.PUT, RequestMethod.PATCH })
	// @PutMapping("/{id}")
	public ResponseEntity<Issue> doCover(@PathVariable("id") long issueId) {
		log.info("Получен запрос на возврат: issueId = {}", issueId);

		return ResponseEntity.ok(issueService.cover(issueId));
	}
}
