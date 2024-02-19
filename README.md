## Урок 10. Spring Testing. JUnit и Mockito для написания тестов.

В проекте библиотека написать полноценные API-тесты (с поднятием БД в H2 и WebTestClient) на все ресурсы проекта, т.е.
получение книги, читателя, выдачи, создание книги, читателя, ресурса,...

Безопасность выключить в тестах.

### Решение:

*Тесты endpoints по контроллерам:*

* [class BookControllerIntegrationTest](src/test/java/edu/alexey/spring/library/api/BookControllerIntegrationTest.java)

* [class ReaderControllerIntegrationTest](src/test/java/edu/alexey/spring/library/api/ReaderControllerIntegrationTest.java)

* [class IssueControllerIntegrationTest](src/test/java/edu/alexey/spring/library/api/IssueControllerIntegrationTest.java)

*Тестирование:*

![Eclipse/JUnit 5](https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw10-testing/tests-stat-1.png)

![mvn test](https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw10-testing/tests-stat2.png)


## Урок 9. Spring Cloud. Микросервисная архитектура.

1\. Восстановить пример, рассмотренный на уроке (запустить эврику и 2 сервиса; заставить их взаимодействовать).

2\.* Добавить третий сервис: сервис читателей.

### Решение:

*Eureka: running micro-services*

![Eureka: running micro-services](https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw9-eureka/eureka_micro-services.png)


## Урок 8. Spring AOP, управление транзакциями.

1\. Создать аннотацию замера времени исполнения метода (Timer). Она может
ставиться над методами или классами.

Аннотация работает так: после исполнения метода (метода класса) с такой
аннотацией, необходимо в лог записать следующее:

	className - methodName #(количество секунд выполнения)

2\.* Создать аннотацию RecoverException, которую можно ставить только над
методами.

	@interface RecoverException {
		Class<? extends RuntimeException>[] noRecoverFor() default {};
	}


У аннотации должен быть параметр *noRecoverFor*, в котором можно перечислить
другие классы исключений.

Аннотация работает так: если во время исполнения метода был экспешн, то не
прокидывать его выше и возвращать из метода значение по умолчанию
(*null, 0, false, ...*).
При этом, если тип исключения входит в список перечисленных в noRecoverFor
исключений, то исключение НЕ прерывается и прокидывается выше.

3\.*** Параметр *noRecoverFor* должен учитывать иерархию исключений.


## Урок 7. Spring Security. Работа с JWT. Защита от основных видов атак.

1\. Для ресурсов, возвращающих HTML-страницы, реализовать авторизацию через login-форму.
Остальные /api ресурсы, возвращающие JSON, закрывать не нужно!

2.1* Реализовать таблицы User(id, name, password) и Role(id, name), связанные многие ко многим

2.2* Реализовать UserDetailsService, который предоставляет данные из БД (таблицы User и Role)

3.3* Ресурсы выдачей (issue) доступны обладателям роли admin

3.4* Ресурсы читателей (reader) доступны всем обладателям роли reader

3.5* Ресурсы книг (books) доступны всем авторизованным пользователям

### Решение:

* [class User](src/main/java/edu/alexey/spring/library/entities/User.java)

* [class Role](src/main/java/edu/alexey/spring/library/entities/Role.java)

* [реализация Spring Security configuration](src/main/java/edu/alexey/spring/library/security/SecurityConfigurer.java)

* [реализация UserDetailsService](src/main/java/edu/alexey/spring/library/security/UserDetailsServiceImpl.java)

* [class Role](src/main/java/edu/alexey/spring/library/entities/Role.java)

* [SQL наполнения БД тестовыми данными, включая новые таблицы Пользователей, Ролей и связующую таблицу](src/main/resources/data.sql)


## Урок 6. Проектирование и реализация API для серверного приложения.

1. Подключить OpenAPI 3 и swagger к проекту с библиоткой
2. Описать все контроллеры, эндпоинты и возвращаемые тела с помощью аннотаций OpenAPI 3
3. В качестве результата, необходимо прислать скриншот(ы) страницы swagger (с ручками)

### Решение:

Рефакторинг API:

* Именование endpoints приведено к общепринятому (collections<->plural nouns).
* HTTP-методы *DELETE* теперь возвращают 204 без данных удалённой сущности в теле вместо 200 с данными сущности.
* HTTP-методы *CREATE* теперь возвращают 201 без данных созданной сущности в теле вместо 200 с данными сущности.

*Swagger - OpenAPI definition*

<a target="_blank" href="https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw6-openapi/openapi-overview.png">
	<img src="https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw6-openapi/openapi-overview.png" alt="Short" style="width: 400px; float: left;"/>
</a>

<a target="_blank" href="https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw6-openapi/openapi-in-detail.png">
	<img src="https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw6-openapi/openapi-in-detail.png" alt="Short" style="width: 400px; float: left;"/>
</a>
<br style="clear: both">


## Урок 5. Spring Data для работы с базами данных

* К проекту подключена СУБД *H2*.

* Два варианта работы с СУБД:

    * На базе JPA&nbsp;&mdash; ветка *main*.
    
    * На базе JDBC&nbsp;&mdash; ветка *hw5-data-jdbc*.


## Урок 4. Spring MVC. Использование шаблонизатора Thymeleaf

В предыдущий проект добавить следующие ресурсы, которые возвращают готовые HTML-страницы:

1. `/ui/books`&nbsp;&mdash; на странице должен отобразиться список всех доступных книг в системе.

2. `/ui/reader`&nbsp;&mdash; аналогично 1.1

3. `/ui/issues`&nbsp;&mdash; на странице отображается таблица, в которой есть столбцы (книга, читатель, когда взял, когда вернул (если не вернул - пустая ячейка)).

4. `/ui/reader/{id}`&nbsp;&mdash; страница, где написано имя читателя с идентификатором id и перечислены книги, которые на руках у этого читателя

### Решение:

Контроллеры для Веба &nbsp;&mdash; [edu.alexey.spring.library.web](src/main/java/edu/alexey/spring/library/web/)

Шаблоны&nbsp;&mdash; [src/main/resources/templates](src/main/resources/templates)

*Страница Книги:*

![Книги](https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw4-thymeleaf/books.png)

*Страница Читатели:*

![Читатели](https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw4-thymeleaf/readers.png)

*Страница Выдачи книг:*

![Выдачи](https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw4-thymeleaf/issue_descriptions.png)

*Страница Книг удерживаемых читателем:*

![Удерживаемые книги](https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw4-thymeleaf/books_held_by_reader.png)


## Урок 3. Использование Spring для разработки серверного приложения

1\. Доделать сервис управления книгами:

1.1. Реализовать контроллер по управлению книгами с ручками:
* `GET /book/{id}`&nbsp;&mdash; получить описание книги,
* `DELETE /book/{id}`&nbsp;&mdash; удалить книгу,
* `POST /book`&nbsp;&mdash; создать книгу.

1.2. Реализовать контроллер по управлению читателями (аналогично контроллеру с книгами из 1.1).

1.3. В контроллере `IssueController` добавить ресурс `GET /issue/{id}`&nbsp;&mdash; получить описание факта выдачи.

2\.

2.1. В сервис `IssueService` добавить проверку, что у пользователя на руках нет книг. Если есть&nbsp;&mdash; не выдавать книгу (статус ответа&nbsp;&mdash; *409 Conflict*).

2.2. В сервис читателя добавить ручку `GET /reader/{id}/issue`&nbsp;&mdash; вернуть список всех выдачей для данного читателя.

3\.

3.1. В `Issue` поле `timestamp` разбить на 2: `issued_at`, `returned_at` - дата выдачи и дата возврата.

3.2. К ресурс `POST /issue` добавить запрос `PUT /issue/{issueId}`, который закрывает факт выдачи (т.е. проставляет `returned_at` в `Issue`).

\*Замечание: возвращенные книги НЕ нужно учитывать при 2.1

3.3. Пункт 2.1 расширить параметром, сколько книг может быть на руках у пользователя. Должно задаваться в конфигурации (параметр `application.issue.max-allowed-books`). Если параметр не задан, то использовать значение 1.

### Решение:

Контроллеры&nbsp;&mdash; [edu.alexey.spring.library.api](src/main/java/edu/alexey/spring/library/api/)

Сервисы&nbsp;&mdash; [edu.alexey.spring.library.services](src/main/java/edu/alexey/spring/library/services/)

Сущности&nbsp;&mdash; [edu.alexey.spring.library.entities](src/main/java/edu/alexey/spring/library/entities/)

Обработка исключений в отдельном *@RestControllerAdvice*&nbsp;&mdash; [CommonExceptionsAdvice](src/main/java/edu/alexey/spring/library/api/CommonExceptionsAdvice.java):

* В ситуациях, когда запрос ссылается на несуществующий элемент по ключу -> *NOT_FOUND (404)*
* При попытке погасить уже погашенную выдачу книги -> *PRECONDITION_FAILED (412)*
* При прочих запросах с некорректно заданными данными -> *FORBIDDEN (403)*

*Некорректные данные в запросе:*

![Wrong request](https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw3-library/ex_wrong_request.png)

*Нет сущности, подразумеваемой в запросе:*

![No entry](https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw3-library/ex_no_entry.png)

*Попытка погасить уже погашенную выдачу:"

![Already covered issue](https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw3-library/ex_already_covered_issue.png)

*Попытка получить больше книг, чем разрешено:"

![Issue limit reached](https://raw.githubusercontent.com/alexeycoder/illustrations/main/java-spring-hw3-library/ex_issue_limit_reached.png)


