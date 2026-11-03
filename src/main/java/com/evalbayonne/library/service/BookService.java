package com.evalbayonne.library.service;

import com.evalbayonne.library.dto.book.BookRequest;
import com.evalbayonne.library.dto.book.BookResponse;

import java.util.List;

public interface BookService {
    List<BookResponse> findAll();

    List<BookResponse> findByAuthor(Long authorId);

    BookResponse findById(Long id);

    BookResponse create(BookRequest request);

    BookResponse update(Long id, BookRequest request);

    void delete(Long id);
}
