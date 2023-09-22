package com.hana.sugang.api.member.controller;

import com.hana.sugang.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 시큐리티의 로그인을 직접 수행해보기 위한 클래스.
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/model/")
public class ModelMemberController {
    private final MemberService memberService;


    @GetMapping("/member/login")
    private String member() {
        return "member/login";
    }
}
