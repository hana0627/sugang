package com.hana.sugang.api.member.repository;

import com.hana.sugang.api.member.domain.Member;

import java.util.Optional;
import java.util.Set;

public interface MemberRepositoryCustom {
    Optional<Member> findBYUsernameWithQuery(String username);

    void decreaseCurrentScore(Set<Long> Ids, Integer score);
}
