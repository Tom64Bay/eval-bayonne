package com.evalbayonne.library.repository;

import com.evalbayonne.library.domain.Loan;
import com.evalbayonne.library.domain.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByMemberId(Long memberId);

    boolean existsByBookIdAndStatus(Long bookId, LoanStatus status);
}
