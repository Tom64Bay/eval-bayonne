package com.evalbayonne.library.service.impl;

import com.evalbayonne.library.domain.Member;
import com.evalbayonne.library.dto.member.MemberRequest;
import com.evalbayonne.library.dto.member.MemberResponse;
import com.evalbayonne.library.exception.BusinessRuleException;
import com.evalbayonne.library.exception.ResourceNotFoundException;
import com.evalbayonne.library.mapper.MemberMapper;
import com.evalbayonne.library.repository.MemberRepository;
import com.evalbayonne.library.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream()
                .map(MemberMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse findById(Long id) {
        return MemberMapper.toResponse(getMemberOrThrow(id));
    }

    @Override
    public MemberResponse create(MemberRequest request) {
        memberRepository.findByEmail(request.email()).ifPresent(existing -> {
            throw new BusinessRuleException("Un adherent existe deja avec l'email " + request.email());
        });
        Member member = MemberMapper.toEntity(request);
        return MemberMapper.toResponse(memberRepository.save(member));
    }

    @Override
    public MemberResponse update(Long id, MemberRequest request) {
        Member member = getMemberOrThrow(id);
        memberRepository.findByEmail(request.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessRuleException("Un adherent existe deja avec l'email " + request.email());
                });
        MemberMapper.updateEntity(member, request);
        return MemberMapper.toResponse(memberRepository.save(member));
    }

    @Override
    public void delete(Long id) {
        Member member = getMemberOrThrow(id);
        memberRepository.delete(member);
    }

    private Member getMemberOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adherent introuvable avec l'id " + id));
    }
}
