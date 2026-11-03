package com.evalbayonne.library.service.impl;

import com.evalbayonne.library.domain.Author;
import com.evalbayonne.library.domain.Book;
import com.evalbayonne.library.domain.LoanStatus;
import com.evalbayonne.library.dto.book.BookRequest;
import com.evalbayonne.library.dto.book.BookResponse;
import com.evalbayonne.library.exception.ResourceNotFoundException;
import com.evalbayonne.library.mapper.BookMapper;
import com.evalbayonne.library.repository.AuthorRepository;
import com.evalbayonne.library.repository.BookRepository;
import com.evalbayonne.library.repository.LoanRepository;
import com.evalbayonne.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final LoanRepository loanRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream()
                .map(this::toResponseWithAvailability)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> findByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId).stream()
                .map(this::toResponseWithAvailability)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        return toResponseWithAvailability(getBookOrThrow(id));
    }

    @Override
    public BookResponse create(BookRequest request) {
        Author author = getAuthorOrThrow(request.authorId());
        Book book = BookMapper.toEntity(request, author);
        return toResponseWithAvailability(bookRepository.save(book));
    }

    @Override
    public BookResponse update(Long id, BookRequest request) {
        Book book = getBookOrThrow(id);
        Author author = getAuthorOrThrow(request.authorId());
        BookMapper.updateEntity(book, request, author);
        return toResponseWithAvailability(bookRepository.save(book));
    }

    @Override
    public void delete(Long id) {
        Book book = getBookOrThrow(id);
        bookRepository.delete(book);
    }

    private BookResponse toResponseWithAvailability(Book book) {
        boolean available = !loanRepository.existsByBookIdAndStatus(book.getId(), LoanStatus.ACTIVE);
        return BookMapper.toResponse(book, available);
    }

    private Book getBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livre introuvable avec l'id " + id));
    }

    private Author getAuthorOrThrow(Long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Auteur introuvable avec l'id " + authorId));
    }
}
