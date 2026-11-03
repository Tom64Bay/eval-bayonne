package com.evalbayonne.library.dto.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record AuthorRequest(
        @NotBlank(message = "Le prenom est obligatoire") String firstName,
        @NotBlank(message = "Le nom est obligatoire") String lastName,
        @Past(message = "La date de naissance doit etre dans le passe") LocalDate birthDate
) {
}
