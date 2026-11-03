package com.evalbayonne.library.dto.book;

public record BookResponse(
        Long id,
        String isbn,
        String title,
        Integer publicationYear,
        Long authorId,
        String authorFullName,
        boolean available
) {
}
