package com.evalbayonne.library.mapper;

import com.evalbayonne.library.domain.Author;
import com.evalbayonne.library.domain.Book;
import com.evalbayonne.library.dto.book.BookRequest;
import com.evalbayonne.library.dto.book.BookResponse;

public final class BookMapper {

    private BookMapper() {
    }

    public static Book toEntity(BookRequest request, Author author) {
        return Book.builder()
                .isbn(request.isbn())
                .title(request.title())
                .publicationYear(request.publicationYear())
                .author(author)
                .build();
    }

    public static void updateEntity(Book book, BookRequest request, Author author) {
        book.setIsbn(request.isbn());
        book.setTitle(request.title());
        book.setPublicationYear(request.publicationYear());
        book.setAuthor(author);
    }

    public static BookResponse toResponse(Book book, boolean available) {
        Author author = book.getAuthor();
        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getPublicationYear(),
                author.getId(),
                author.getFirstName() + " " + author.getLastName(),
                available
        );
    }
}
