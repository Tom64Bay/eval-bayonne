package com.evalbayonne.library.repository;

import com.evalbayonne.library.domain.Author;
import com.evalbayonne.library.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void shouldFindBooksByAuthorId() {
        Author author = authorRepository.save(Author.builder()
                .firstName("Albert")
                .lastName("Camus")
                .build());

        bookRepository.save(Book.builder()
                .isbn("isbn-1")
                .title("L'Etranger")
                .publicationYear(1942)
                .author(author)
                .build());
        bookRepository.save(Book.builder()
                .isbn("isbn-2")
                .title("La Peste")
                .publicationYear(1947)
                .author(author)
                .build());

        List<Book> books = bookRepository.findByAuthorId(author.getId());

        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("L'Etranger", "La Peste");
    }

    @Test
    void shouldFindBookByIsbn() {
        Author author = authorRepository.save(Author.builder()
                .firstName("Albert")
                .lastName("Camus")
                .build());
        bookRepository.save(Book.builder()
                .isbn("isbn-unique")
                .title("La Chute")
                .publicationYear(1956)
                .author(author)
                .build());

        assertThat(bookRepository.findByIsbn("isbn-unique")).isPresent();
        assertThat(bookRepository.findByIsbn("inexistant")).isEmpty();
    }
}
