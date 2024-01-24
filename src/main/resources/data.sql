INSERT INTO readers (name)
VALUES
	('Екатерина Кондратьева'),
	('Александр Чернов'),
	('Лев Егоров'),
	('Валерия Соловьева'),
	('Екатерина Майорова'),
	('Виктория Карасева'),
	('София Романова'),
	('Амина Мартынова'),
	('Матвей Баранов'),
	('Михаил Максимов');

INSERT INTO books (name)
VALUES
	('Александр Дюма - Граф Монте-Кристо'),
	('Михаил Булгаков - Записки юного врача'),
	('Александр Дюма - Три мушкетёра'),
	('Теодор Драйзер - Американская трагедия'),
	('Энн Бронте - Незнакомка из Уайлдфелл-Холла'),
	('Стефан Цвейг - Нетерпение сердца'),
	('Айн Рэнд - Атлант расправил плечи'),
	('Иван Тургенев - Отцы и дети'),
	('Чарльз Диккенс - Дэвид Копперфилд'),
	('Лев Толстой - Воскресение'),
	('Лев Толстой - Война и мир'),
	('Александр Пушкин - Пиковая дама'),
	('Пелам Вудхаус - Дживс, вы — гений!');

INSERT INTO issues (book_id, reader_id, issued_at, returned_at)
VALUES
	(1, 5, TIMESTAMPADD(DAY,-2, CURRENT_TIMESTAMP()), NULL),
	(3, 5, TIMESTAMPADD(DAY,-2, CURRENT_TIMESTAMP()), CURRENT_TIMESTAMP()),
	(5, 5, TIMESTAMPADD(DAY,-2, CURRENT_TIMESTAMP()), CURRENT_TIMESTAMP()),
	(2, 8, TIMESTAMPADD(DAY,-3, CURRENT_TIMESTAMP()), NULL),
	(4, 8, TIMESTAMPADD(DAY,-3, CURRENT_TIMESTAMP()), NULL),
	(10,9, TIMESTAMPADD(DAY,-5, CURRENT_TIMESTAMP()), TIMESTAMPADD(DAY,-1, CURRENT_TIMESTAMP()));
