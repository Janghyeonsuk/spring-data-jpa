package study.datajpa.repository;

import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MemberQueryRepository memberQueryRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("test")
    public void testMember() {
        System.out.println("memberRepository = " + memberRepository);
        //given
        Member member = new Member("memberA", 10);
        Member savedMember = memberRepository.save(member);

        //when
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        //then
        assertThat(savedMember.getId()).isEqualTo(findMember.getId());
        assertThat(savedMember.getUsername()).isEqualTo(findMember.getUsername());
        assertThat(savedMember).isEqualTo(findMember);
    }

    @Test
    public void basicCrud() {
        //given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        //then
        assertThat(member1).isEqualTo(findMember1);
        assertThat(member2).isEqualTo(findMember2);

        //????????? ?????? ??????
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findTop3HelloBy();

    }

    @Test
    public void testNamedQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void findUsernameList() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);
        List<String> usernameList = memberRepository.findUsernameList();
        assertThat(usernameList.get(0)).isEqualTo("AAA");
        assertThat(usernameList.get(1)).isEqualTo("BBB");
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10);
        member1.setTeam(team);
        memberRepository.save(member1);

        List<MemberDto> dtoList = memberRepository.findMemberDto();
        for (MemberDto dto : dtoList) {
            assertThat(dto.getTeamName()).isEqualTo(member1.getTeam().getName());
        }
    }

    @Test
    public void findByNames() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);
        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            assertThat(member.getUsername()).isIn(Arrays.asList("AAA", "BBB"));
        }
    }

    @Test
    public void returnType() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);
        // ?????? list??? ?????????????????? ???????????? ????????? ????????? empty collection??? ?????????
        List<Member> collection = memberRepository.findListByUsername("AAA");
        System.out.println("collection = " + collection);
        //?????? ???????????? ????????? ????????? Null??? ?????????
        //?????? ???????????? ????????? ????????? ?????? ??????
        Member member = memberRepository.findMemberByUsername("AAA");
        System.out.println("member = " + member);

        Optional<Member> optional = memberRepository.findOptionalByUsername("AAA");
        System.out.println("optional = " + optional.get());

    }

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        //??????????????????JPA??? Page Index??? 0?????? ??????
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findPageByAge(age, pageRequest);

        //API?????? ??????(Dto??? ??????)
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
        //slice??? count ?????? ?????? limit + 1
        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);

        //then
        List<Member> pageContent = page.getContent();
        List<Member> sliceContent = slice.getContent();


        for (Member member : pageContent) {
            System.out.println("member = " + member);
        }

        assertThat(pageContent.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

        assertThat(sliceContent.size()).isEqualTo(3);
        assertThat(slice.getNumber()).isEqualTo(0);
        assertThat(slice.isFirst()).isTrue();
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush(); //???????????? ???????????? ?????? ?????? DB??? ??????
//        em.clear(); //????????? ??????????????? ??????

        //??????????????? ?????? ????????? ??????????????? ???????????? DB??? ????????? ?????? -> ??????????????? ?????? ????????? ??????????????? ????????????
        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5.getAge());

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when N + 1
        //select Member 1
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

    }

    @Test
    public void queryHint() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        // dirty checking?????? ????????? ???????????? ?????? ????????????????????? ?????? ??????
//        Member findMember = memberRepository.findById(member1.getId()).get();
//        findMember.setUsername("member2");
        Member findMember = memberRepository.findReadOnlyByUsername("member1"); //?????? ??????
        findMember.setUsername("member2"); // ???????????? ????????? ?????? ??????, ???????????? ????????? ?????? -> ?????? ?????????

        em.flush();
    }

    @Test
    public void lock() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findLockByUsername("member1"); //?????? ??????
        findMember.setUsername("member2");

        em.flush();
    }

    //????????? ?????? Repository ??????
    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic() throws Exception {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        Specification<Member> spec =
                MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void basic() throws Exception {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        em.persist(new Member("m1", 0, teamA));
        em.persist(new Member("m2", 0, teamA));
        em.flush();

        //when
        //Probe ??????
        Member member = new Member("m1");
        Team team = new Team("teamA"); //?????????????????? teamA ??????
        member.setTeam(team);

        //ExampleMatcher ??????, age ??????????????? ??????
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        //then
        assertThat(result.size()).isEqualTo(1);
    }


    //Projections -> ????????? ???????????? ??? ???????????? ?????? ???
    @Test
    public void projections() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        em.persist(new Member("m1", 0, teamA));
        em.persist(new Member("m2", 0, teamA));
        em.flush();

        //when
        //???????????? ????????? ROOT?????? JPQL SELECT??? ?????????
        List<UserNameOnly> result = memberRepository.findProjectionsByUsername("m1", UserNameOnly.class);//?????????????????? ?????? -> ????????? ??????
        List<UserNameOnlyDto> resultDto = memberRepository.findProjectionsByUsername("m1", UserNameOnlyDto.class); //???????????? ?????? -> ?????? ??????

        //???????????? ????????? ROOT??? ????????? LEFT OUTER JOIN ?????? ??? ?????? ????????? ??????????????? ????????? ????????? ?????? -> ????????? X(????????????)
        List<NestedClosedProjections> nested = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

        for (UserNameOnly userNameOnly : result) {
            System.out.println("userNameOnly = " + userNameOnly.getUsername()); //????????? ??????
        }

        for (UserNameOnlyDto userNameOnlyDto : resultDto) {
            System.out.println("userNameOnlyDto = " + userNameOnlyDto.getUsername()); //?????? ??????
        }

        for (NestedClosedProjections nestedClosedProjections : nested) {
            String username = nestedClosedProjections.getUsername();
            String teamName = nestedClosedProjections.getTeam().getName();
            System.out.println("username = " + username);
            System.out.println("teamName = " + teamName);
        }

    }

    @Test
    public void nativeQuery() {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        em.persist(new Member("m1", 0, teamA));
        em.persist(new Member("m2", 0, teamA));
        em.flush();
        em.clear();

        //when

        Member result = memberRepository.findByNativeQuery("m1");
        System.out.println("result = " + result);

        //Dto
        Page<MemberProjection> nativeProjection = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = nativeProjection.getContent();

        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection.getId() = " + memberProjection.getId());
            System.out.println("memberProjection.getUsername() = " + memberProjection.getUsername());
            System.out.println("memberProjection.getTeamName() = " + memberProjection.getTeamName());
        }
    }

    @Test
    public void dynamicNativeQuery() {
        //given
        String sql = "select m.username as username from member m";
        List<MemberDto> result = em.createNativeQuery(sql)
                .setFirstResult(0)
                .setMaxResults(10)
                .unwrap(NativeQuery.class)
                .addScalar("username")
                .setResultTransformer(Transformers.aliasToBean(MemberDto.class))
                .getResultList();
    }

}