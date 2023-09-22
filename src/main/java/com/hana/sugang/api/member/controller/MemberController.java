package com.hana.sugang.api.member.controller;

import com.hana.sugang.api.member.dto.request.MemberCrate;
import com.hana.sugang.api.member.dto.response.MemberResponse;
import com.hana.sugang.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;


    @GetMapping("/members")
    private List<MemberResponse> memberList() {
        return memberService.findMembers();
    }
    @GetMapping("/member/{userName}")
    private MemberResponse member(@PathVariable(name = "userName") String userName) {
        return memberService.searchUser(userName);
    }

    @PostMapping("/member")
    private Map<String,Long> createMember(@RequestBody MemberCrate requestDto) {
        Map<String,Long> map = new HashMap<>();
        Long id = memberService.saveMember(requestDto);
        map.put("id",id);
        return map;
    }

}
