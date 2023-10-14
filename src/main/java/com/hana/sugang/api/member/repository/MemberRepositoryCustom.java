package com.hana.sugang.api.member.repository;

import com.hana.sugang.api.member.domain.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.Set;

public interface MemberRepositoryCustom {
    @Lock(LockModeType.NONE)
    Optional<Member> findBYUsernameWithQuery(String username);

    void decreaseCurrentScore(Set<Long> Ids, Integer score);

    Optional<Member> findByUsernameRedis(String username);
}
