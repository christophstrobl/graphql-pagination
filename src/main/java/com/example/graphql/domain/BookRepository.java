package com.example.graphql.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Christoph Strobl
 */
public interface BookRepository extends ListCrudRepository<Book, String>, PagingAndSortingRepository<Book, String> {

	List<Book> findByTitleContains(String title);

	Page<Book> findByTitleContains(String title, Pageable pageable);

	Window<Book> findTop5By(ScrollPosition scrollPosition);

	Window<Book> findTop5ByTitleContains(String title, ScrollPosition scrollPosition);

	Window<Book> findTop5ByOrderByPublicationDate(ScrollPosition scrollPosition);

}
