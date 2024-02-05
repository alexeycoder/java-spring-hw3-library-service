package edu.alexey.spring.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Сервер, отвечающий за выдачу книг в библиотеке.
 * 
 * /book/** - книга GET /book/25 - получить книгу с идентификатором 25
 * 
 * /reader/** - читатель
 * 
 * /issue/** - информация о выдаче POST /issue {"readerId": 25, "bookId": 57} -
 * выдать читателю с идентификатором 25 книгу с идентификатором 57
 */
@SpringBootApplication
public class LibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);

		//		SecurityContext context = SecurityContextHolder.getContext();
	}
}
