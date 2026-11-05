package com.evalbayonne.library.mapper;

import com.evalbayonne.library.domain.Book;
import com.evalbayonne.library.domain.Loan;
import com.evalbayonne.library.domain.Member;
import com.evalbayonne.library.dto.loan.LoanResponse;

public final class LoanMapper {

    private LoanMapper() {
    }

    public static LoanResponse toResponse(Loan loan) {
        Book book = loan.getBook();
        Member member = loan.getMember();
        return new LoanResponse(
                loan.getId(),
                book.getId(),
                book.getTitle(),
                member.getId(),
                member.getFirstName() + " " + member.getLastName(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus(),
                loan.getRenewalCount()
        );
    }
}
