package com.evalbayonne.library.dto.author;

import java.time.LocalDate;

public record AuthorResponse(
        Long id,
        String firstName,
        String lastName,
        LocalDate birthDate
) {
}
