package com.evalbayonne.library.service.impl;

import com.evalbayonne.library.domain.Author;
import com.evalbayonne.library.domain.Book;
import com.evalbayonne.library.domain.LoanStatus;
import com.evalbayonne.library.dto.book.BookRequest;
import com.evalbayonne.library.dto.book.BookResponse;
import com.evalbayonne.library.exception.ResourceNotFoundException;
import com.evalbayonne.library.repository.AuthorRepository;
import com.evalbayonne.library.repository.BookRepository;
import com.evalbayonne.library.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Author author;
    private Book book;

    @BeforeEach
    void setUp() {
        author = Author.builder().id(1L).firstName("Victor").lastName("Hugo").build();
        book = Book.builder().id(10L).isbn("978-2-07-036057-6").title("Les Miserables")
                .publicationYear(1862).author(author).build();
    }

    @Test
    void shouldReturnBookWithAvailabilityWhenNoActiveLoan() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(loanRepository.existsByBookIdAndStatus(10L, LoanStatus.ACTIVE)).thenReturn(false);

        BookResponse response = bookService.findById(10L);

        assertThat(response.available()).isTrue();
        assertThat(response.title()).isEqualTo("Les Miserables");
    }

    @Test
    void shouldReturnBookAsUnavailableWhenActiveLoanExists() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(loanRepository.existsByBookIdAndStatus(10L, LoanStatus.ACTIVE)).thenReturn(true);

        BookResponse response = bookService.findById(10L);

        assertThat(response.available()).isFalse();
    }

    @Test
    void shouldThrowWhenBookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldCreateBookWhenAuthorExists() {
        BookRequest request = new BookRequest("978-2-07-036057-6", "Les Miserables", 1862, 1L);
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(loanRepository.existsByBookIdAndStatus(anyLong(), any())).thenReturn(false);

        BookResponse response = bookService.create(request);

        assertThat(response.isbn()).isEqualTo("978-2-07-036057-6");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void shouldThrowWhenCreatingBookWithUnknownAuthor() {
        BookRequest request = new BookRequest("isbn", "title", 2020, 42L);
        when(authorRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(bookRepository, never()).save(any());
    }
}
