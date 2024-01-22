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

