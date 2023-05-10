package com.example.graphql.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

/**
 * @author Christoph Strobl
 */
@Data
@Entity
@Immutable
@Table(name = "books_view")
public class BookView {

	@Id
	private String id;
	private String title;
	private String isbn10;
	private Date publicationDate;

	private String authorId;
	@Column(name = "first_name")
	private String authorFirstName;
	@Column(name = "last_name")
	private String authorLastName;

}
