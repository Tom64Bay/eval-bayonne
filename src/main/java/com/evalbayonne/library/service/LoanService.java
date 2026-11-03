package com.evalbayonne.library.service;

import com.evalbayonne.library.dto.loan.LoanRequest;
import com.evalbayonne.library.dto.loan.LoanResponse;

import java.util.List;

public interface LoanService {
    List<LoanResponse> findAll();

    List<LoanResponse> findByMember(Long memberId);

    LoanResponse findById(Long id);

    LoanResponse createLoan(LoanRequest request);

    LoanResponse returnLoan(Long id);

    LoanResponse renewLoan(Long id);
}
