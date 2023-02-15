package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UserNameOnly {
//    @Value("#{target.username + ' ' + target.age}") //OpenProjection
    String getUsername(); // closeProjection -> select 최적화
}
