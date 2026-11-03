package com.evalbayonne.library.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookRequest(
        @NotBlank(message = "L'ISBN est obligatoire") String isbn,
        @NotBlank(message = "Le titre est obligatoire") String title,
        @Positive(message = "L'annee de publication doit etre positive") Integer publicationYear,
        @NotNull(message = "L'identifiant de l'auteur est obligatoire") Long authorId
) {
}
