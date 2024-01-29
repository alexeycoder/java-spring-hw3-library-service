DROP TABLE IF EXISTS books CASCADE;

DROP TABLE IF EXISTS readers CASCADE;

DROP TABLE IF EXISTS issues CASCADE;

CREATE TABLE books (
    book_id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(255) NOT NULL
);

CREATE TABLE readers (
    reader_id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(255) NOT NULL
);

CREATE TABLE issues (
    issue_id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    book_id bigint NOT NULL,
    reader_id bigint NOT NULL,
    issued_at timestamp(6) NOT NULL,
    returned_at timestamp(6)
);
