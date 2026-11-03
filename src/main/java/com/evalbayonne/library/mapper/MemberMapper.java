package com.evalbayonne.library.mapper;

import com.evalbayonne.library.domain.Member;
import com.evalbayonne.library.dto.member.MemberRequest;
import com.evalbayonne.library.dto.member.MemberResponse;

public final class MemberMapper {

    private MemberMapper() {
    }

    public static Member toEntity(MemberRequest request) {
        return Member.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
    }

    public static void updateEntity(Member member, MemberRequest request) {
        member.setFirstName(request.firstName());
        member.setLastName(request.lastName());
        member.setEmail(request.email());
    }

    public static MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getRegistrationDate()
        );
    }
}
