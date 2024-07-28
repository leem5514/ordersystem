package com.example.ordersystem.member.controller;

import com.example.ordersystem.common.auth.JwtTokenProvider;
import com.example.ordersystem.common.dto.CommonResDto;
import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.dto.MemberListDto;
import com.example.ordersystem.member.dto.MemberLoginDto;
import com.example.ordersystem.member.dto.MemberSaveDto;
import com.example.ordersystem.member.repository.MemberRepository;
import com.example.ordersystem.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@RequestMapping("/member")
@RestController
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public MemberController(MemberService memberService, MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }


//    @GetMapping("/member/create")
//    public String memberCreateForm() {
//        return "member/create";
//    }

    @PostMapping("/member/create") // 생성
    public ResponseEntity<Object> memberCreate(@Valid @RequestBody MemberSaveDto dto) {
        Member member = memberService.memberCreate(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "success created", member.getId());

        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    @GetMapping("/member/list")
    public ResponseEntity<?> memberList(Pageable pageable) {
        Page<MemberListDto> memberListDtos = memberService.memberList(pageable);

        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "success created", memberListDtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
    @PostMapping("/member/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginDto dto) {
        // email , pwd 일치 검증
        Member member = memberService.login(dto);

        // if 일치할경우 accessToken 생성
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        // 생성된 토큰을 CommonResDto 에 담아서 사용자에게 return
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Login is Successful", loginInfo);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

}
