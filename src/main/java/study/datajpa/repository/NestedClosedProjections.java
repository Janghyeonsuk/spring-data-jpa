package study.datajpa.repository;

//중첩구조 -> 멤버 + 팀
public interface NestedClosedProjections {
    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }

}
