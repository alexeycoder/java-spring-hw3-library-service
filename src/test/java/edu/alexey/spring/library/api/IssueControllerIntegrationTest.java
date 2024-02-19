package edu.alexey.spring.library.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import edu.alexey.spring.library.api.dto.IssueCreationDto;
import edu.alexey.spring.library.api.dto.IssueDescriptionResponceDto;
import edu.alexey.spring.library.entities.Book;
import edu.alexey.spring.library.entities.Issue;
import edu.alexey.spring.library.entities.Reader;
import edu.alexey.spring.library.repositories.BookRepository;
import edu.alexey.spring.library.repositories.IssueRepository;
import edu.alexey.spring.library.repositories.ReaderRepository;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
		"application.max-allowed-books=" + IssueControllerIntegrationTest.MAX_ALLOWED_BOOKS
})
@AutoConfigureWebClient
class IssueControllerIntegrationTest {

	private static final String ENDPOINT_BASE = "/issues";
	static final int MAX_ALLOWED_BOOKS = 5;

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private ReaderRepository readerRepository;
	@Autowired
	private IssueRepository issueRepository;

	private List<Book> books;
	private List<Reader> readers;
	private List<Issue> issues;

	@BeforeEach
	void setUp() throws Exception {

		issueRepository.deleteAll();
		readerRepository.deleteAll();
		bookRepository.deleteAll();

		this.books = bookRepository.saveAllAndFlush(List.of(
				new Book(null, "book a"),
				new Book(null, "book b"),
				new Book(null, "book c")));

		this.readers = readerRepository.saveAllAndFlush(List.of(
				new Reader(null, "Вася"),
				new Reader(null, "Маша"),
				new Reader(null, "Петя")));

		this.issues = issueRepository.saveAllAndFlush(List.of(
				new Issue(null, books.get(0).getBookId(), readers.get(0).getReaderId(),
						LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(5)),
				new Issue(null, books.get(1).getBookId(), readers.get(0).getReaderId(),
						LocalDateTime.now().minusDays(8), null),
				new Issue(null, books.get(2).getBookId(), readers.get(0).getReaderId(),
						LocalDateTime.now().minusDays(5), null),
				new Issue(null, books.get(0).getBookId(), readers.get(1).getReaderId(),
						LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(10)),
				new Issue(null, books.get(1).getBookId(), readers.get(1).getReaderId(),
						LocalDateTime.now().minusDays(15), LocalDateTime.now().minusDays(9))));
	}

	private static LocalDateTime truncToSeconds(LocalDateTime localDateTime) {
		return localDateTime == null ? null : localDateTime.truncatedTo(ChronoUnit.SECONDS);
	}

	@Test
	void descriptionByIdReturnsCorrectDescriptionIfIssueExists() {

		for (Issue issue : issues) {

			webTestClient.get().uri(ENDPOINT_BASE + "/" + issue.getIssueId())
					.exchange()
					.expectStatus().isOk()
					.expectBody(IssueDescriptionResponceDto.class)
					.value(responceDto -> {

						assertThat(responceDto)

								.returns(issue.getIssueId(),
										from(IssueDescriptionResponceDto::issueId))

								.returns(truncToSeconds(issue.getIssuedAt()),
										from(IssueDescriptionResponceDto::issuedAt)
												.andThen(IssueControllerIntegrationTest::truncToSeconds))

								.returns(truncToSeconds(issue.getReturnedAt()),
										from(IssueDescriptionResponceDto::returnedAt)
												.andThen(IssueControllerIntegrationTest::truncToSeconds))

								.extracting(dto -> dto.book().bookId(), dto -> dto.reader().readerId())
								.containsExactly(issue.getBookId(), issue.getReaderId());
					});
		}
	}

	@ParameterizedTest
	@ValueSource(strings = { "ab12", "1a", "0.1" })
	void descriptionByIdReturns400IfInvalidIssueId(String issueIdStr) {

		webTestClient.get().uri(ENDPOINT_BASE + "/" + issueIdStr)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void descriptionByIdReturns404IfNoSuchIssue() {

		long nonexistentId = Math.addExact(10,
				issueRepository.findAll().stream().mapToLong(Issue::getIssueId).max().getAsLong());

		webTestClient.get().uri(ENDPOINT_BASE + "/" + nonexistentId)
				.exchange()
				.expectStatus().isNotFound();
	}

	static Example<Issue> uncoveredAndBelongsToReaderIssueExample(long readerId) {
		Issue anUncoveredIssue = new Issue();
		anUncoveredIssue.setReaderId(readerId);
		anUncoveredIssue.setReturnedAt(null);
		return Example.of(anUncoveredIssue,
				ExampleMatcher.matching().withIgnorePaths("issueId", "bookId", "issuedAt").withIncludeNullValues());
	}

	@Test
	void countHeldBooksByReaderIdReturnsCorrectAnswer() {

		long testReaderId = readers.get(0).getReaderId();

		long countBooksHeldByTestReader = issueRepository.count(uncoveredAndBelongsToReaderIssueExample(testReaderId));

		assertThat(countBooksHeldByTestReader)
				.isEqualTo(issues.stream().filter(i -> i.getReaderId() == testReaderId && i.getReturnedAt() == null)
						.count());

		webTestClient.get().uri(ENDPOINT_BASE + "/held/" + testReaderId)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Long.class).isEqualTo(countBooksHeldByTestReader);
	}

	@ParameterizedTest
	@ValueSource(strings = { "ab12", "1a", "0.1" })
	void countHeldBooksByReaderIdReturns400IfInvalidReaderId(String readerIdStr) {

		webTestClient.get().uri(ENDPOINT_BASE + "/held/" + readerIdStr)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void countHeldBooksByReaderIdReturns404IfNoSuchReader() {

		long nonexistentId = Math.addExact(10,
				readerRepository.findAll().stream().mapToLong(Reader::getReaderId).max().getAsLong());

		webTestClient.get().uri(ENDPOINT_BASE + "/held/" + nonexistentId)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	void doIssueReturns201WithLocationOnSuccess() {

		Book book = books.get(0);
		Reader reader = readers.getLast();
		IssueCreationDto requestBodyValue = new IssueCreationDto(book.getBookId(), reader.getReaderId(),
				LocalDateTime.now());

		webTestClient.post().uri(ENDPOINT_BASE)
				.bodyValue(requestBodyValue)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().valueMatches("Location", ENDPOINT_BASE + "/\\d+")
				.expectBody().isEmpty();
	}

	private static Map<String, String> issueRequestRepr(String bookIdStr, String readerIdStr, String issuedAtStr) {
		return Map.of(
				"bookId", bookIdStr,
				"readerId", readerIdStr,
				"issuedAt", issuedAtStr);
	}

	@ParameterizedTest
	@ValueSource(strings = { "ab12", "1a", "0.1" })
	void doIssueReturns400IfInvalidBookId(String bookIdStr) {

		var requestBodyValue = issueRequestRepr(
				bookIdStr,
				readers.getLast().getReaderId().toString(),
				LocalDateTime.now().toString());

		webTestClient.post().uri(ENDPOINT_BASE)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(requestBodyValue)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@ParameterizedTest
	@ValueSource(strings = { "ab12", "1a", "0.1" })
	void doIssueReturns400IfInvalidReaderId(String readerIdStr) {

		var requestBodyValue = issueRequestRepr(
				books.getFirst().getBookId().toString(),
				readerIdStr,
				LocalDateTime.now().toString());

		webTestClient.post().uri(ENDPOINT_BASE)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(requestBodyValue)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void doIssueReturns404IfNoSuchBook() {

		long nonexistentBookId = Math.addExact(10,
				bookRepository.findAll().stream().mapToLong(Book::getBookId).max().getAsLong());

		var requestBodyValue = issueRequestRepr(
				Long.toString(nonexistentBookId),
				readers.getLast().getReaderId().toString(),
				LocalDateTime.now().toString());

		webTestClient.post().uri(ENDPOINT_BASE)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(requestBodyValue)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	void doIssueReturns404IfNoSuchReader() {

		Long nonexistentReaderId = Math.addExact(10,
				readerRepository.findAll().stream().mapToLong(Reader::getReaderId).max().getAsLong());

		var requestBodyValue = issueRequestRepr(
				books.getLast().getBookId().toString(),
				nonexistentReaderId.toString(),
				LocalDateTime.now().toString());

		webTestClient.post().uri(ENDPOINT_BASE)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(requestBodyValue)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	void doIssueReturns409IfExceededAllowedAmountOfHeldBooks() {

		Reader greedyReader = readerRepository.saveAndFlush(new Reader(null, "Greedy Reader"));
		String bookIdStr = books.getLast().getBookId().toString();

		for (int i = 1; i <= MAX_ALLOWED_BOOKS + 1; ++i) {
			var requestBodyValue = issueRequestRepr(
					bookIdStr,
					greedyReader.getReaderId().toString(),
					LocalDateTime.now().toString());
			var responseSpec = webTestClient.post().uri(ENDPOINT_BASE)
					.accept(MediaType.APPLICATION_JSON)
					.bodyValue(requestBodyValue)
					.exchange();

			if (i <= MAX_ALLOWED_BOOKS) {
				responseSpec.expectStatus().isCreated();
			} else {
				responseSpec.expectStatus().isEqualTo(HttpStatus.CONFLICT);
			}
		}
	}

	@Test
	void doCoverReturns200OnSuccessAndIssueBecomesCovered() {

		Book book = books.getLast();
		Reader reader = readers.getLast();
		Issue uncoveredIssue = issueRepository
				.saveAndFlush(new Issue(
						null,
						book.getBookId(),
						reader.getReaderId(),
						LocalDateTime.now(),
						null));

		webTestClient.patch().uri(ENDPOINT_BASE + "/" + uncoveredIssue.getIssueId())
				.exchange()
				.expectStatus().isOk();

		// Issue mustBeCoveredIssue = issueRepository.findById(uncoveredIssue.getIssueId()).get();
		// assertThat(mustBeCoveredIssue.getReturnedAt()).isNotNull();
		webTestClient.get().uri(ENDPOINT_BASE + "/" + uncoveredIssue.getIssueId())
				.exchange()
				.expectStatus().isOk()
				.expectBody(IssueDescriptionResponceDto.class)
				.value(description -> assertThat(description.returnedAt()).isNotNull());
	}

	@ParameterizedTest
	@ValueSource(strings = { "ab12", "1a", "0.1" })
	void doCoverReturns400IfInvalidIssueId(String issueIdStr) {

		webTestClient.patch().uri(ENDPOINT_BASE + "/" + issueIdStr)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void doCoverReturns404IfNoSuchIssue() {

		long nonexistentIssueId = Math.addExact(10,
				issueRepository.findAll().stream().mapToLong(Issue::getIssueId).max().getAsLong());

		webTestClient.patch().uri(ENDPOINT_BASE + "/" + nonexistentIssueId)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	void doCoverReturns412IfAlreadyCovered() {

		Book book = books.getLast();
		Reader reader = readers.getLast();
		Issue coveredIssue = issueRepository
				.saveAndFlush(new Issue(
						null,
						book.getBookId(),
						reader.getReaderId(),
						LocalDateTime.now().minusDays(10),
						LocalDateTime.now()));

		webTestClient.patch().uri(ENDPOINT_BASE + "/" + coveredIssue.getIssueId())
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.PRECONDITION_FAILED);
	}
}
