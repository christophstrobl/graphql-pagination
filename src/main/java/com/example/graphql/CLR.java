/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.graphql;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import com.example.graphql.domain.Author;
import com.example.graphql.domain.AuthorRepository;
import com.example.graphql.domain.Book;
import com.example.graphql.domain.BookRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Strobl
 */
@Component
class CLR implements CommandLineRunner {

	private BookRepository books;
	private AuthorRepository authors;

	public CLR(BookRepository books, AuthorRepository authors) {

		this.books = books;
		this.authors = authors;
	}

	@Override
	public void run(String... args) throws Exception {

		Faker faker = new Faker();

		IntStream.range(0, 10).forEach(it -> {

			Author author = new Author();
			author.setId("author-%s".formatted(it));
			author.setFirstName(faker.name().firstName());
			author.setLastName(faker.name().lastName());

			authors.save(author);
		});

		IntStream.range(0, 100)
				.forEach(it -> {

					Book book = new Book();
					book.setId("book-%03d".formatted(it));
					book.setTitle(faker.book().title());
					book.setIsbn10(UUID.randomUUID().toString().substring(0, 10));
					book.setPublicationDate(LocalDate.ofInstant(faker.date().past(5000, TimeUnit.DAYS).toInstant(), ZoneId.systemDefault()));
					book.setAuthorId("author-%s".formatted(new Random().nextInt(10)));

					books.save(book);
				});
	}
}
