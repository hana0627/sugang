package com.hana.sugang.api.member.repository;

import com.hana.sugang.api.member.domain.Member;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;

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

    public void decreaseCurrentScore(Set<Long> ids, Integer score) {
        NumberTemplate<Integer> subtractedScore = Expressions.numberTemplate(
                Integer.class, "{0} - {1}",
                member.currentScore,
                ConstantImpl.create(score));

        queryFactory.update(member)
                .set(member.currentScore, subtractedScore)
                .where(member.id.in(ids)).execute();
    }

    @Override
    public Optional<Member> findByUsernameRedis(String username) {
        return Optional.ofNullable(queryFactory.select(member)
                .from(member)
                .where(member.username.eq(username))
                .setLockMode(LockModeType.NONE)
                .fetchOne());
    }
}
