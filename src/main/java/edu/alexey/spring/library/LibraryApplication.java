package edu.alexey.spring.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

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
@EnableAspectJAutoProxy
public class LibraryApplication {

	public static void main(String[] args) {

		SpringApplication.run(LibraryApplication.class, args);

		//try (ConfigurableApplicationContext context = SpringApplication.run(LibraryApplication.class, args)) {
		//	EntityManager em = context.getBean(EntityManager.class);
		//	if (em != null) {
		//		em.createNativeQuery("SELECT * FROM users_roles").getResultList()
		//				.forEach(r -> System.out.println(Arrays.toString((Object[]) r)));
		//	}
		//
		//	RoleService roleService = context.getBean(RoleService.class);
		//	UserService userService = context.getBean(UserService.class);
		//
		//	System.out.println("====USERSERVICE.FINDALL()====");
		//	System.out.println(userService.findAll());
		//
		//	System.out.println("====ROLESERVICE.FINDALL()====");
		//	System.out.println(roleService.findAll());
		//
		//	System.out.println("====FIND_BY_USERNAME====");
		//	System.out.println(
		//			userService.findByUsername("Pasha").get().getRoles().stream().map(Role::getRoleName).toList());
		//	System.out.println(
		//			userService.findByUsername("Dasha").get().getRoles().stream().map(Role::getRoleName).toList());
		//	System.out.println(
		//			userService.findByUsername("Vasya").get().getRoles().stream().map(Role::getRoleName).toList());
		//	System.out
		//			.println(userService.findByUsername("Klavrentiy").get().getRoles().stream().map(Role::getRoleName)
		//					.toList());
		//}
	}
}
