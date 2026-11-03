package com.evalbayonne.library.mapper;

import com.evalbayonne.library.domain.Author;
import com.evalbayonne.library.dto.author.AuthorRequest;
import com.evalbayonne.library.dto.author.AuthorResponse;

public final class AuthorMapper {

    private AuthorMapper() {
    }

    public static Author toEntity(AuthorRequest request) {
        return Author.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .birthDate(request.birthDate())
                .build();
    }

    public static void updateEntity(Author author, AuthorRequest request) {
        author.setFirstName(request.firstName());
        author.setLastName(request.lastName());
        author.setBirthDate(request.birthDate());
    }

    public static AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                author.getBirthDate()
        );
    }
}
