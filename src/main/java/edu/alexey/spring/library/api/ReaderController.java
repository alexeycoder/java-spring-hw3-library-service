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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Reader", description = "The Reader API")
@RequiredArgsConstructor
@RestController("api.ReaderController")
@RequestMapping("/readers")
public class ReaderController {

	private final ReaderService readerService;

	@Operation(summary = "Gets all readers")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "All readers of the library", content = {
			@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Reader.class))) }) })
	@GetMapping()
	List<Reader> all() {
		return readerService.getAll();
	}

	@Operation(summary = "Get reader by id", description = "Reader must exist")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "The reader found", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Reader.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Reader not found", content = @Content) })
	@GetMapping("/{id}")
	ResponseEntity<Reader> one(@PathVariable("id") long readerId) {
		return ResponseEntity.ok(readerService.getById(readerId));
	}

	@Operation(summary = "Add new reader")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "The created reader URL in Location field", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid reader data supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Reader not found", content = @Content) })
	@PostMapping()
	ResponseEntity<Void> addNew(@RequestBody Reader reader) {

		Reader addedEntry = readerService.addNew(reader);
		return ResponseEntity
				.created(UriComponentsBuilder.fromPath("/readers/{id}")
						.buildAndExpand(addedEntry.getReaderId())
						.toUri())
				.build();
	}

	@Operation(summary = "Delete reader by id", description = "Reader must exist")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "The reader deletion confirmation", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Reader not found", content = @Content) })
	@DeleteMapping("/{id}")
	ResponseEntity<Void> delete(@PathVariable("id") long readerId) {
		readerService.deleteById(readerId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Find issues by reader id", description = "Reader must exist")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All issues to the reader", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Issue.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Reader not found", content = @Content) })
	@GetMapping("/{id}/issues")
	public List<Issue> issuesByReaderId(@PathVariable("id") long readerId) {
		return readerService.findAllIssuesByReaderId(readerId);
	}
}
