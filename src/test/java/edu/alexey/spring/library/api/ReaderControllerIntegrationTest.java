package edu.alexey.spring.library.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import edu.alexey.spring.library.api.dto.IssueResponseDto;
import edu.alexey.spring.library.api.dto.ReaderResponseDto;
import edu.alexey.spring.library.entities.Book;
import edu.alexey.spring.library.entities.Issue;
import edu.alexey.spring.library.entities.Reader;
import edu.alexey.spring.library.repositories.BookRepository;
import edu.alexey.spring.library.repositories.IssueRepository;
import edu.alexey.spring.library.repositories.ReaderRepository;
import io.netty.util.internal.ThreadLocalRandom;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
class ReaderControllerIntegrationTest {

	private static final String ENDPOINT_BASE = "/readers";

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private ReaderRepository readerRepository;
	@Autowired
	private IssueRepository issueRepository;

	private List<Reader> readers;

	/**
	 * Исходное состояние каждого теста: 3 элемента в БД читателей.
	 */
	@BeforeEach
	void setUp() {

		readerRepository.deleteAll();

		List<Reader> rawInput = List.of(
				new Reader(null, "Вася"),
				new Reader(null, "Маша"),
				new Reader(null, "Петя"));

		this.readers = readerRepository.saveAll(rawInput);
	}

	@Test
	void allReturnsCorrectItems() {

		var responseList = webTestClient.get().uri(ENDPOINT_BASE)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(ReaderResponseDto.class)
				.hasSize(readers.size())
				.returnResult().getResponseBody();

		assertThat(responseList)
				.usingRecursiveComparison()
				.ignoringCollectionOrder()
				.isEqualTo(readers);
	}

	@Test
	void allReturnsEmptyListIfNoEntries() {

		readerRepository.deleteAll();

		webTestClient.get().uri(ENDPOINT_BASE)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(ReaderResponseDto.class)
				.hasSize(0);
	}

	@Test
	void oneReturnsCorrectEntryForValidId() {
		for (Reader reader : readers) {
			Long readerId = reader.getReaderId();

			var responseBody = webTestClient.get().uri(ENDPOINT_BASE + "/" + readerId)
					.exchange()
					.expectStatus().isOk()
					.expectBody(ReaderResponseDto.class)
					.returnResult().getResponseBody();

			assertThat(responseBody).usingRecursiveComparison().isEqualTo(reader);
		}
	}

	@ParameterizedTest
	@ValueSource(strings = { "ab12", "1a", "0.1" })
	void oneReturns400ForInvalidId(String readerIdStr) {

		webTestClient.get().uri(ENDPOINT_BASE + "/" + readerIdStr)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void oneReturns404IfNotFound() {

		Long nonexistentId = Math.addExact(10,
				readerRepository.findAll().stream().mapToLong(Reader::getReaderId).max().getAsLong());

		webTestClient.get().uri(ENDPOINT_BASE + "/" + nonexistentId)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	void addNewAffectsRepoSizeAsExpected() {

		long initSize = readerRepository.count();

		var invalidReaders = List.of(
				new Reader(null, null),
				new Reader(null, ""),
				new Reader(null, "   "));

		var validReaders = List.of(
				new Reader(null, "reader a"),
				new Reader(null, "reader b"),
				new Reader(null, "reader c"));

		Stream.concat(validReaders.stream(), invalidReaders.stream())
				.forEach(r -> {
					webTestClient.post()
							.uri(ENDPOINT_BASE)
							.bodyValue(r).exchange();
				});

		long resultingSize = readerRepository.count();

		assertThat(resultingSize).isEqualTo(initSize + validReaders.size());
	}

	@Test
	void addNewReturns201ForValidInput() {

		Reader requestBodyValue = new Reader(null, "Some Reader");

		webTestClient.post()
				.uri(ENDPOINT_BASE)
				.bodyValue(requestBodyValue)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().valueMatches("Location", ENDPOINT_BASE + "/\\d+")
				.expectBody().isEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "   \t\t  " })
	void addNewReturns400ForBlankBookName(String name) {

		Reader requestBodyValue = new Reader(null, name);

		webTestClient.post()
				.uri(ENDPOINT_BASE)
				.bodyValue(requestBodyValue)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void deleteReturns204IfSuccess() {

		Long randomEntryId = readers.get(ThreadLocalRandom.current().nextInt(readers.size())).getReaderId();

		webTestClient.delete()
				.uri(ENDPOINT_BASE + "/" + randomEntryId)
				.exchange()
				.expectStatus().isNoContent()
				.expectBody().isEmpty();
	}

	@Test
	void deleteAffectsRepoSizeAsExpected() {

		long initSize = readerRepository.count();
		assertThat(initSize).isEqualTo(readers.size());

		long maxId = readers.stream().mapToLong(Reader::getReaderId).max().getAsLong();
		long nonexistentId = Math.addExact(maxId, 10);

		webTestClient.delete()
				.uri(ENDPOINT_BASE + "/" + nonexistentId)
				.exchange();

		assertThat(readerRepository.count()).isEqualTo(initSize);

		for (Reader reader : readers) {
			webTestClient.delete()
					.uri(ENDPOINT_BASE + "/" + reader.getReaderId())
					.exchange();
		}

		assertThat(readerRepository.count()).isZero();
	}

	@ParameterizedTest
	@ValueSource(strings = { "a", "1a", "1.0" })
	void deleteReturns400ForInvalidId(String readerIdStr) {

		webTestClient.delete()
				.uri(ENDPOINT_BASE + "/" + readerIdStr)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void deleteReturns404IfNotFound() {

		long maxId = readers.stream().mapToLong(Reader::getReaderId).max().getAsLong();
		long nonexistentId = Math.addExact(maxId, 10);

		webTestClient.delete()
				.uri(ENDPOINT_BASE + "/" + nonexistentId)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	void issuesByReaderIdReturnsCorrectListIfIssuesExist() {

		Reader reader = readers.getLast();
		bookRepository.deleteAll();
		Book book = bookRepository.saveAndFlush(new Book(null, "Some Book"));
		issueRepository.deleteAll();
		var issues = issueRepository.saveAllAndFlush(List.of(
				new Issue(null, book.getBookId(), reader.getReaderId(),
						LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), null),
				new Issue(null, book.getBookId(), reader.getReaderId(),
						LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), null)));

		List<IssueResponseDto> responseList = webTestClient.get()
				.uri(ENDPOINT_BASE + "/" + reader.getReaderId() + "/issues")
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(IssueResponseDto.class)
				.hasSize(issues.size())
				.returnResult().getResponseBody();

		assertThat(responseList)
				.usingRecursiveComparison()
				.ignoringCollectionOrder()
				.isEqualTo(issues);
	}

	@Test
	void issuesByReaderIdReturnsEmptyListIfNoIssues() {

		Reader reader = readers.get(0);

		webTestClient.get()
				.uri(ENDPOINT_BASE + "/" + reader.getReaderId() + "/issues")
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(IssueResponseDto.class)
				.hasSize(0);
	}

	@Test
	void issuesByReaderIdReturns404IfNoReader() {

		Long nonexistentId = Math.addExact(10,
				readerRepository.findAll().stream().mapToLong(Reader::getReaderId).max().getAsLong());

		webTestClient.get()
				.uri(ENDPOINT_BASE + "/" + nonexistentId + "/issues")
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(IssueResponseDto.class)
				.hasSize(0);
	}

	@ParameterizedTest
	@ValueSource(strings = { "a", "1a", "1.0" })
	void issuesByReaderIdReturns400IfInvalidReaderId(String readerIdStr) {

		webTestClient.get()
				.uri(ENDPOINT_BASE + "/" + readerIdStr + "/issues")
				.exchange()
				.expectStatus().isBadRequest();
	}

	@ParameterizedTest(name = "{index} => for httpMethod ''{0}''")
	@MethodSource("edu.alexey.spring.library.api.BookControllerIntegrationTest#httpMethodProvider")
	void unspecifiedButRequiredIdInAnyEndpointResults404(HttpMethod httpMethod) {

		webTestClient.method(httpMethod)
				.uri(ENDPOINT_BASE + "/") // id is forgotten
				.exchange()
				.expectStatus().isNotFound();
	}

}
