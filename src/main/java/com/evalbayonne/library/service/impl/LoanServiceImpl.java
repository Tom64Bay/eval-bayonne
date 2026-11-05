package com.evalbayonne.library.service.impl;

import com.evalbayonne.library.domain.Book;
import com.evalbayonne.library.domain.Loan;
import com.evalbayonne.library.domain.LoanStatus;
import com.evalbayonne.library.domain.Member;
import com.evalbayonne.library.dto.loan.LoanRequest;
import com.evalbayonne.library.dto.loan.LoanResponse;
import com.evalbayonne.library.exception.BusinessRuleException;
import com.evalbayonne.library.exception.ResourceNotFoundException;
import com.evalbayonne.library.mapper.LoanMapper;
import com.evalbayonne.library.repository.BookRepository;
import com.evalbayonne.library.repository.LoanRepository;
import com.evalbayonne.library.repository.MemberRepository;
import com.evalbayonne.library.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanServiceImpl implements LoanService {

    private static final int LOAN_DURATION_DAYS = 14;
    private static final int MAX_ACTIVE_LOANS_PER_MEMBER = 3;
    private static final int MAX_RENEWALS = 2;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> findAll() {
        return loanRepository.findAll().stream()
                .map(LoanMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> findByMember(Long memberId) {
        return loanRepository.findByMemberId(memberId).stream()
                .map(LoanMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LoanResponse findById(Long id) {
        return LoanMapper.toResponse(getLoanOrThrow(id));
    }

    @Override
    public LoanResponse createLoan(LoanRequest request) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Livre introuvable avec l'id " + request.bookId()));
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Adherent introuvable avec l'id " + request.memberId()));

        if (loanRepository.existsByBookIdAndStatus(book.getId(), LoanStatus.ACTIVE)) {
            throw new BusinessRuleException("Le livre '" + book.getTitle() + "' est deja emprunte");
        }

        //long activeLoansForMember = loanRepository.countByMemberIdAndStatus(member.getId(), LoanStatus.ACTIVE);
        long activeLoansForMember = 4; // local mock for testing purposes
        if (activeLoansForMember > MAX_ACTIVE_LOANS_PER_MEMBER) {
            throw new BusinessRuleException(
                    "L'adherent '" + member.getLastName() + "' a atteint son nombre maximum de prets actifs");
        }

        LocalDate today = LocalDate.now();
        Loan loan = Loan.builder()
                .book(book)
                .member(member)
                .loanDate(today)
                .dueDate(today.plusDays(LOAN_DURATION_DAYS))
                .status(LoanStatus.ACTIVE)
                .build();

        return LoanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public LoanResponse returnLoan(Long id) {
        Loan loan = getLoanOrThrow(id);

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new BusinessRuleException("Ce pret a deja ete cloture");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);

        return LoanMapper.toResponse(loanRepository.save(loan));
    }

    @Override
    public LoanResponse renewLoan(Long id) {
        Loan loan = getLoanOrThrow(id);

        if (loan.getRenewalCount() >= MAX_RENEWALS) {
            throw new BusinessRuleException("Ce pret a deja atteint le nombre maximum de renouvellements");
        }

        loan.setDueDate(LocalDate.now().plusDays(LOAN_DURATION_DAYS));
        loan.setRenewalCount(loan.getRenewalCount() + 1);

        return LoanMapper.toResponse(loanRepository.save(loan));
    }

    private Loan GetLoanOrThrow(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pret introuvable avec l'id " + id));
    }
}
