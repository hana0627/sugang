package com.hana.sugang.api.member.repository;

import com.hana.sugang.api.member.domain.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.hana.sugang.api.member.domain.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findBYUsernameWithQuery(String username) {
        return Optional.ofNullable(queryFactory.select(member)
                .from(member)
                .where(member.username.eq(username))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne());
    }
}
