package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    //도메인 컨버터 ※조회용으로만 사용해야함 (트랜잭션이 없는 범위에서 엔티티를 조회했으므로 DB에 반영이 안됨
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable Member member) {
        return member.getUsername();
    }

    //localhost:8080/members?page=0 -> (디폴트값) 20개만 꺼내준다
    //localhost:8080/members?page=0&size=3 -> 3개만 꺼내줌
    //localhost:8080/members?page=0&size=3&sort=id,desc -> sort도 가능
    //localhost:8080/members?page=0&size=3&sort=id,desc&sort=username,desc
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
//        PageRequest request = PageRequest.of(1, 5); //1. page 시작을 0이아닌 1로 하는 방법 -> 직접 생성
        Page<Member> page = memberRepository.findAll(pageable);
//        Page<Member> page = memberRepository.findAll(request);
        Page<MemberDto> map = page.map(MemberDto::new);
        return map;
    }

//    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
