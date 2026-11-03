package com.evalbayonne.library.controller;

import com.evalbayonne.library.dto.book.BookRequest;
import com.evalbayonne.library.dto.book.BookResponse;
import com.evalbayonne.library.exception.ResourceNotFoundException;
import com.evalbayonne.library.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    void shouldReturnBookList() throws Exception {
        BookResponse response = new BookResponse(1L, "isbn-1", "Dune", 1965, 10L, "Frank Herbert", true);
        when(bookService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Dune"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void shouldReturn404WhenBookNotFound() throws Exception {
        when(bookService.findById(99L)).thenThrow(new ResourceNotFoundException("Livre introuvable avec l'id 99"));

        mockMvc.perform(get("/api/books/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Livre introuvable avec l'id 99"));
    }

    @Test
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        BookRequest invalidRequest = new BookRequest("", "", null, null);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateBook() throws Exception {
        BookRequest request = new BookRequest("isbn-1", "Dune", 1965, 10L);
        BookResponse response = new BookResponse(1L, "isbn-1", "Dune", 1965, 10L, "Frank Herbert", true);
        when(bookService.create(any(BookRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Dune"));
    }
}
