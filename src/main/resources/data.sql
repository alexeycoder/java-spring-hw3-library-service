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

INSERT INTO users (username, password)
VALUES
	('Паша', '$2a$10$C4kF8VZqTu7ds3KLG6ZKmOq5ls3.k0KVU3xfJkNKsReSGeNT5Idbm'),		-- password
	('Даша', '$2a$10$tXUU2Gcd12b9zUbrkqkaROZt4eDwfHJlZ3lX.Q66Zyj1W3JVcTxmG'),		-- пароль   
	('Вася', '$2a$10$4z/u0ZuQW7oU5tSV/Uk4HO5y.ODTVxc0HHpYV2zvIxmtgPVRDtnaK'),		-- ляляля   
	('Клаврентий', '$2a$10$aUaTww2ZgQgA7YEdcQUOzOIB7Nzv7q5trGMvhm1PpJh0Ktc0Pfmpu');	-- 12345    

ALTER TABLE roles ADD CONSTRAINT roles_contraint CHECK (role_name in ('ADMIN', 'READER')); -- self control
	
INSERT INTO roles (role_id, role_name)
VALUES
	(1, 'ADMIN'),
	(2, 'READER');

INSERT INTO user_roles (user_id, role_id)
	SELECT user_id, 2 AS role_id FROM users WHERE username = 'Паша';

INSERT INTO user_roles (user_id, role_id)
	SELECT user_id, 2 AS role_id FROM users WHERE username = 'Даша';

INSERT INTO user_roles (user_id, role_id)
	SELECT user_id, 2 AS role_id FROM users WHERE username = 'Вася';

INSERT INTO user_roles (user_id, role_id)
	SELECT user_id, 1 AS role_id FROM users WHERE username = 'Клаврентий';
	
	
	
	
	