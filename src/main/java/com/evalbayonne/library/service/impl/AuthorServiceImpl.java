package com.evalbayonne.library.service.impl;

import com.evalbayonne.library.domain.Author;
import com.evalbayonne.library.dto.author.AuthorRequest;
import com.evalbayonne.library.dto.author.AuthorResponse;
import com.evalbayonne.library.exception.ResourceNotFoundException;
import com.evalbayonne.library.mapper.AuthorMapper;
import com.evalbayonne.library.repository.AuthorRepository;
import com.evalbayonne.library.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AuthorResponse> findAll() {
        return authorRepository.findAll().stream()
                .map(AuthorMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponse findById(Long id) {
        return AuthorMapper.toResponse(getAuthorOrThrow(id));
    }

    @Override
    public AuthorResponse create(AuthorRequest request) {
        Author author = AuthorMapper.toEntity(request);
        return AuthorMapper.toResponse(authorRepository.save(author));
    }

    @Override
    public AuthorResponse update(Long id, AuthorRequest request) {
        Author author = getAuthorOrThrow(id);
        AuthorMapper.updateEntity(author, request);
        return AuthorMapper.toResponse(authorRepository.save(author));
    }

    @Override
    public void delete(Long id) {
        Author author = getAuthorOrThrow(id);
        authorRepository.delete(author);
    }

    private Author getAuthorOrThrow(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auteur introuvable avec l'id " + id));
    }
}
