package com.evalbayonne.library.dto.loan;

import jakarta.validation.constraints.NotNull;

public record LoanRequest(
        @NotNull(message = "L'identifiant du livre est obligatoire") Long bookId,
        @NotNull(message = "L'identifiant de l'adherent est obligatoire") Long memberId
) {
}
