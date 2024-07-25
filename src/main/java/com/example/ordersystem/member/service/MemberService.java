package com.example.ordersystem.member.service;

import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.dto.MemberListDto;
import com.example.ordersystem.member.dto.MemberSaveDto;
import com.example.ordersystem.member.repository.MemberRepository;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    //private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
//        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public Member memberCreate(MemberSaveDto dto) {

        Member member = dto.toEntity();
        Member savedMember = memberRepository.save(member);
        return savedMember;
    }
    @Transactional
    public Page<MemberListDto> memberList(Pageable pageable) {
        Page<Member> memberListDtos = memberRepository.findAll(pageable);
        return memberListDtos.map(a -> a.listFromEntity());
    }
}
