package com.evalbayonne.library.service.impl;

import com.evalbayonne.library.domain.Book;
import com.evalbayonne.library.domain.Loan;
import com.evalbayonne.library.domain.LoanStatus;
import com.evalbayonne.library.domain.Member;
import com.evalbayonne.library.dto.loan.LoanRequest;
import com.evalbayonne.library.dto.loan.LoanResponse;
import com.evalbayonne.library.exception.BusinessRuleException;
import com.evalbayonne.library.exception.ResourceNotFoundException;
import com.evalbayonne.library.repository.BookRepository;
import com.evalbayonne.library.repository.LoanRepository;
import com.evalbayonne.library.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Book book;
    private Member member;

    @BeforeEach
    void setUp() {
        book = Book.builder().id(1L).title("Dune").isbn("isbn-1").build();
        member = Member.builder().id(2L).firstName("Paul").lastName("Atreides").build();
    }

    @Test
    void shouldCreateLoanWhenBookIsAvailable() {
        LoanRequest request = new LoanRequest(1L, 2L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));
        when(loanRepository.existsByBookIdAndStatus(1L, LoanStatus.ACTIVE)).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanResponse response = loanService.createLoan(request);

        assertThat(response.status()).isEqualTo(LoanStatus.ACTIVE);
        assertThat(response.dueDate()).isEqualTo(response.loanDate().plusDays(14));
    }

    @Test
    void shouldRejectLoanWhenBookAlreadyLoaned() {
        LoanRequest request = new LoanRequest(1L, 2L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));
        when(loanRepository.existsByBookIdAndStatus(1L, LoanStatus.ACTIVE)).thenReturn(true);

        assertThatThrownBy(() -> loanService.createLoan(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("deja emprunte");

        verify(loanRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenBookDoesNotExist() {
        LoanRequest request = new LoanRequest(99L, 2L);
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.createLoan(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldMarkLoanAsReturned() {
        Loan loan = Loan.builder().id(5L).book(book).member(member)
                .loanDate(LocalDate.now().minusDays(3))
                .dueDate(LocalDate.now().plusDays(11))
                .status(LoanStatus.ACTIVE)
                .build();
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanResponse response = loanService.returnLoan(5L);

        assertThat(response.status()).isEqualTo(LoanStatus.RETURNED);
        assertThat(response.returnDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void shouldRejectReturningAlreadyReturnedLoan() {
        Loan loan = Loan.builder().id(5L).book(book).member(member)
                .loanDate(LocalDate.now().minusDays(20))
                .dueDate(LocalDate.now().minusDays(6))
                .returnDate(LocalDate.now().minusDays(5))
                .status(LoanStatus.RETURNED)
                .build();
        when(loanRepository.findById(5L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.returnLoan(5L))
                .isInstanceOf(BusinessRuleException.class);

        verify(loanRepository, never()).save(any());
    }
}
