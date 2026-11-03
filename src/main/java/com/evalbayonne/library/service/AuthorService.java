package com.evalbayonne.library.service;

import com.evalbayonne.library.dto.author.AuthorRequest;
import com.evalbayonne.library.dto.author.AuthorResponse;

import java.util.List;

public interface AuthorService {
    List<AuthorResponse> findAll();

    AuthorResponse findById(Long id);

    AuthorResponse create(AuthorRequest request);

    AuthorResponse update(Long id, AuthorRequest request);

    void delete(Long id);
}
