package com.evalbayonne.library.service;

import com.evalbayonne.library.dto.member.MemberRequest;
import com.evalbayonne.library.dto.member.MemberResponse;

import java.util.List;

public interface MemberService {
    List<MemberResponse> findAll();

    MemberResponse findById(Long id);

    MemberResponse create(MemberRequest request);

    MemberResponse update(Long id, MemberRequest request);

    void delete(Long id);
}
