package com.evalbayonne.library.dto.member;

import java.time.LocalDate;

public record MemberResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        LocalDate registrationDate
) {
}
