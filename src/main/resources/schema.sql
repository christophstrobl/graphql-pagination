create table if not exists authors (first_name varchar(255), id varchar(255) not null, last_name varchar(255), primary key (id));
create table books (publication_date timestamp(6), author_id varchar(255), id varchar(255) not null, isbn10 varchar(255), title varchar(255), primary key (id));
create view if not exists books_view AS SELECT b.id, b.title, b.isbn10, b.author_id, a.first_name, a.last_name from books b INNER JOIN authors a ON b.author_id = a.id;
