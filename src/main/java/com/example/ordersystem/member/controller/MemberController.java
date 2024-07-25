package com.example.ordersystem.member.controller;

import com.example.ordersystem.common.dto.CommonResDto;
import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.dto.MemberListDto;
import com.example.ordersystem.member.dto.MemberSaveDto;
import com.example.ordersystem.member.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
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

}
