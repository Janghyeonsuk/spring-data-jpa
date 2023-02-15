package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * 등록일
 * 수정일
 * 등록자
 * 수정자
 */
@MappedSuperclass //진짜 상속이 아닌 속성 데이터만 상속받는 어노테이션
@Getter
public class JpaBaseEntity {
    //속성 데이터
    @Column(updatable = false, insertable = true)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist //persist 하기전에 발생
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate //update 하기전에 발생
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }

}
