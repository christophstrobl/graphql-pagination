# Getting Started

```bash
mvn spring-boot:run
open http://localhost:8080/graphiql
```

# Requests

**Single `Book` by _id_**

```graphql
query bookDetails {
    bookById(id: "book-001") {
        id
        title
        publicationDate
        author {
            firstName
            lastName
        }
    }
}
```

**List all `Book`s**

```graphql
query bookDetails {
    allBooks {
        id
        title
        publicationDate
        author {
            firstName
            lastName
        }
    }
}
```

**Page through all `Book`s**

```graphql
query bookDetails {
    allBooksPaged(page: 0, size: 5) {
        id
        title
        publicationDate
        author {
            firstName
            lastName
        }
    }
}
```

**Scroll through all `Book`s**

```graphql
query bookDetails {
    allBooksWindowed(cursor: "") {
        edges {
            node {
                id
                title
                publicationDate
                author {
                    firstName
                    lastName
                }
            }
        }
        pageInfo {
            endCursor
            hasNextPage
        }
    }
}
```
