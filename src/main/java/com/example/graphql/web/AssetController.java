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
package com.example.graphql.web;

import java.util.List;

import com.example.graphql.domain.Author;
import com.example.graphql.domain.AuthorRepository;
import com.example.graphql.domain.Book;
import com.example.graphql.domain.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.pagination.CursorStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

/**
 * Controller that exposes the endpoints.
 *
 * @author Christoph Strobl
 * @see <a href="http://localhost:8080/graphiql">http://localhost:8080/graphiql</a>
 */
@Controller
public class AssetController {

	private BookRepository books;
	private AuthorRepository authors;

	private CursorStrategy<ScrollPosition> cursorStrategy;

	public AssetController(BookRepository books, AuthorRepository authors, CursorStrategy<ScrollPosition> cursorStrategy) {

		this.books = books;
		this.authors = authors;
		this.cursorStrategy = cursorStrategy;
	}

	/**
	 * GraphQL schema mapping that allows to resolve {@link Book#getAuthorId()} to an actual {@link Author}.
	 *
	 * @param book
	 * @return
	 */
	@SchemaMapping
	public Author author(Book book) {
		return authors.findById(book.getAuthorId()).get();
	}

	/**
	 * Lookup a single {@link Book} by its id
	 * <pre class="code">
	 * query bookDetails {
	 *   bookById(id: "book-001") {
	 *     id
	 *     title
	 *     publicationDate
	 *     author {
	 *       firstName
	 *       lastName
	 *     }
	 *   }
	 * }
	 * </pre>
	 *
	 * @param id
	 * @return
	 */
	@QueryMapping
	public Book bookById(@Argument String id) {
		return books.findById(id).get();
	}

	/**
	 * Get a {@link List} of all books (obviously not ideal)
	 * <pre class="code">
	 * query bookDetails {
	 *   allBooks {
	 *     id
	 *     title
	 *     publicationDate
	 *     author {
	 *       firstName
	 *       lastName
	 *     }
	 *   }
	 * }
	 * </pre>
	 */
	@QueryMapping
	public List<Book> allBooks() {
		return books.findAll();
	}

	/**
	 * Use an offset/limit approach to page through all {@link Book books}.
	 * <pre class="code">
	 * query bookDetails {
	 *   allBooksPaged(page: 0, size: 5) {
	 *     id
	 *     title
	 *     publicationDate
	 *     author {
	 *       firstName
	 *       lastName
	 *     }
	 *   }
	 * }
	 * </pre>
	 *
	 * @param page
	 * @param size
	 * @return
	 */
	@QueryMapping
	public Page<Book> allBooksPaged(@Argument int page, @Argument int size) {
		return books.findAll(PageRequest.of(page, size));
	}

	@QueryMapping
	public List<Book> booksByTitle(@Argument String title) {
		return books.findByTitleContains(title);
	}

	/**
	 * Use an offset/limit approach to page through {@link Book books} with matching title.
	 * <pre class="code">
	 * query bookDetails {
	 *   booksByTitlePaged(title: "the", page: 0, size: 5) {
	 *     id
	 *     title
	 *     publicationDate
	 *     author {
	 *       firstName
	 *       lastName
	 *     }
	 *   }
	 * }
	 * </pre>
	 *
	 * @param title
	 * @param page
	 * @param size
	 * @return
	 */
	@QueryMapping
	public Page<Book> booksByTitlePaged(@Argument String title, @Argument int page, @Argument int size) {
		return books.findByTitleContains(title, PageRequest.of(page, size));
	}

	/**
	 * Use an index based approach to scroll through all {@link Book books}.
	 * <pre class="code">
	 * query bookDetails {
	 *   allBooksWindowed(cursor: "") {
	 *     edges {
	 *       node {
	 *         id
	 *         title
	 *         publicationDate
	 *         author {
	 *           firstName
	 *           lastName
	 *         }
	 *       }
	 *     }
	 *     pageInfo {
	 *       endCursor
	 *       hasNextPage
	 *     }
	 *   }
	 * }
	 * </pre>
	 *
	 * @param cursor
	 * @return
	 */
	@QueryMapping
	public Window<Book> allBooksWindowed(@Argument String cursor) {

		if (!StringUtils.hasText(cursor)) {
			return books.findTop5By(ScrollPosition.keyset());
		}

		return books.findTop5By(cursorStrategy.fromCursor(cursor));
	}

	/**
	 * Use an index based approach to scroll through matching {@link Book books}.
	 * <pre class="code">
	 * query bookDetails {
	 *   booksByTitleWindowed(title: "the", cursor: "") {
	 *     edges {
	 *       node {
	 *         id
	 *         title
	 *         publicationDate
	 *         author {
	 *           firstName
	 *           lastName
	 *         }
	 *       }
	 *     }
	 *     pageInfo {
	 *       endCursor
	 *       hasNextPage
	 *     }
	 *   }
	 * }
	 * </pre>
	 *
	 * @param title
	 * @param cursor
	 * @return
	 */
	@QueryMapping
	public Window<Book> booksByTitleWindowed(@Argument String title, @Argument String cursor) {

		if (!StringUtils.hasText(cursor)) {
			return books.findTop5ByTitleContains(title, ScrollPosition.keyset());
		}

		return books.findTop5ByTitleContains(title, cursorStrategy.fromCursor(cursor));
	}

	/**
	 * Use an index based approach to scroll through matching {@link Book books}.
	 * <pre class="code">
	 * query bookDetails {
	 *   booksOrderedByDate(cursor: "") {
	 *     edges {
	 *       node {
	 *         id
	 *         title
	 *         publicationDate
	 *         author {
	 *           firstName
	 *           lastName
	 *         }
	 *       }
	 *     }
	 *     pageInfo {
	 *       endCursor
	 *       hasNextPage
	 *     }
	 *   }
	 * }
	 * </pre>
	 *
	 * @param cursor
	 * @return
	 */
	@QueryMapping
	public Window<Book> booksOrderedByDate(@Argument String cursor) {

		if (!StringUtils.hasText(cursor)) {
			return books.findTop5ByOrderByPublicationDate(ScrollPosition.keyset());
		}

		return books.findTop5ByOrderByPublicationDate(cursorStrategy.fromCursor(cursor));
	}
}
