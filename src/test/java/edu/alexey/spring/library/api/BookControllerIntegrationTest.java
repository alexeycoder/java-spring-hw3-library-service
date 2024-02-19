package edu.alexey.spring.library.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import edu.alexey.spring.library.api.dto.BookResponseDto;
import edu.alexey.spring.library.entities.Book;
import edu.alexey.spring.library.repositories.BookRepository;
import io.netty.util.internal.ThreadLocalRandom;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
class BookControllerIntegrationTest {

	private static final String ENDPOINT_BASE = "/books";

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private BookRepository bookRepository;

	@LocalServerPort
	private int port;

	private List<Book> books;

	/**
	 * Исходное состояние каждого теста: 3 элемента в БД.
	 */
	@BeforeEach
	void setUp() {
		System.out.println("Test Port: " + port);

		bookRepository.deleteAll();

		List<Book> rawInput = List.of(
				new Book(null, "book a"),
				new Book(null, "book b"),
				new Book(null, "book c"));

		this.books = bookRepository.saveAll(rawInput);
	}

	@Test
	void allReturnsCorrectItems() {

		var responseList = webTestClient.get().uri(ENDPOINT_BASE)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(BookResponseDto.class)
				.hasSize(books.size())
				.returnResult().getResponseBody();

		assertThat(responseList)
				.usingRecursiveComparison()
				.ignoringCollectionOrder()
				.isEqualTo(books);
	}

	@Test
	void allReturnsEmptyListIfNoEntries() {

		bookRepository.deleteAll();

		webTestClient.get().uri(ENDPOINT_BASE)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(BookResponseDto.class)
				.hasSize(0);
		// .expectBody(new ParameterizedTypeReference<List<BookResponse>>() {})
		// .value(bs -> assertThatCollection(bs).isEmpty());
	}

	@Test
	void oneReturnsCorrectEntryForValidId() {

		for (Book book : books) {
			Long bookId = book.getBookId();

			var responseBody = webTestClient.get().uri(ENDPOINT_BASE + "/" + bookId)
					.exchange()
					.expectStatus().isOk()
					.expectBody(BookResponseDto.class)
					.returnResult().getResponseBody();

			assertThat(responseBody).usingRecursiveComparison().isEqualTo(book);
		}
	}

	@ParameterizedTest
	@ValueSource(strings = { "ab12", "1a", "0.1" })
	void oneReturns400ForInvalidId(String bookIdStr) {

		webTestClient.get().uri(ENDPOINT_BASE + "/" + bookIdStr)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void oneReturns404IfNotFound() {

		Long nonexistentId = Math.addExact(10,
				bookRepository.findAll().stream().mapToLong(Book::getBookId).max().getAsLong());

		webTestClient.get().uri(ENDPOINT_BASE + "/" + nonexistentId)
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	void addNewAffectsRepoSizeAsExpected() {

		long initSize = bookRepository.count();

		var invalidBooks = List.of(
				new Book(null, null),
				new Book(null, ""),
				new Book(null, "   "));

		var validBooks = List.of(
				new Book(null, "a"),
				new Book(null, "b"),
				new Book(null, "c"));

		Stream.concat(validBooks.stream(), invalidBooks.stream())
				.forEach(b -> {
					webTestClient.post()
							.uri(ENDPOINT_BASE)
							.bodyValue(b).exchange();
				});

		long resultingSize = bookRepository.count();

		assertThat(resultingSize).isEqualTo(initSize + validBooks.size());
	}

	@Test
	void addNewReturns201ForValidInput() {

		Book requestBodyValue = new Book(null, "Some Book");

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

		Book requestBodyValue = new Book(null, name);

		webTestClient.post()
				.uri(ENDPOINT_BASE)
				.bodyValue(requestBodyValue)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void deleteReturns204IfSuccess() {

		Long randomEntryId = books.get(ThreadLocalRandom.current().nextInt(books.size())).getBookId();

		webTestClient.delete()
				.uri(ENDPOINT_BASE + "/" + randomEntryId)
				.exchange()
				.expectStatus().isNoContent()
				.expectBody().isEmpty();
	}

	@Test
	void deleteAffectsRepoSizeAsExpected() {

		long initSize = bookRepository.count();
		assertThat(initSize).isEqualTo(books.size());

		long maxId = books.stream().mapToLong(Book::getBookId).max().getAsLong();
		long nonExistingId = Math.addExact(maxId, 10);

		webTestClient.delete()
				.uri(ENDPOINT_BASE + "/" + nonExistingId)
				.exchange();

		assertThat(bookRepository.count()).isEqualTo(initSize);

		for (Book book : books) {
			webTestClient.delete()
					.uri(ENDPOINT_BASE + "/" + book.getBookId())
					.exchange();
		}

		assertThat(bookRepository.count()).isZero();
	}

	@ParameterizedTest
	@ValueSource(strings = { "a", "1a", "1.0" })
	void deleteReturns400ForInvalidId(String bookIdStr) {

		webTestClient.delete()
				.uri(ENDPOINT_BASE + "/" + bookIdStr)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void deleteReturns404IfNotFound() {

		long maxId = books.stream().mapToLong(Book::getBookId).max().getAsLong();
		long nonExistingId = Math.addExact(maxId, 10);

		webTestClient.delete()
				.uri(ENDPOINT_BASE + "/" + nonExistingId)
				.exchange()
				.expectStatus().isNotFound();
	}

	@ParameterizedTest(name = "{index} => for httpMethod ''{0}''")
	@MethodSource("httpMethodProvider")
	//	@MethodSource("org.springframework.http.HttpMethod#values")
	void unspecifiedButRequiredIdInAnyEndpointResults404(HttpMethod httpMethod) {

		//System.out.println("Test httpMethod is " + httpMethod);
		webTestClient.method(httpMethod)
				.uri(ENDPOINT_BASE + "/") // id is forgotten
				.exchange()
				.expectStatus().isNotFound();
	}

	private static Stream<Arguments> httpMethodProvider() {
		return Stream.of(
				Arguments.of(HttpMethod.GET),
				Arguments.of(HttpMethod.POST),
				Arguments.of(HttpMethod.PUT),
				Arguments.of(HttpMethod.PATCH),
				Arguments.of(HttpMethod.DELETE));
	}

}
