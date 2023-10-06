package com.hana.sugang.api.member.repository;

import com.hana.sugang.api.member.domain.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findBYUsernameWithQuery(String username);
}
