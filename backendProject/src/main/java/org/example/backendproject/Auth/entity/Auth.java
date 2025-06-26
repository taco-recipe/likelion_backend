package org.example.backendproject.Auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backendproject.user.entity.User;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity //DB 테이블과 자바 객체를 연결
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) //테이블의 컬럼을 자바 필드와 연결
    private String tokenType;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;




    //테이블과 테이블을 연결      (1대1 관계에서는 연관관계 주인쪽만 패치 전략이 적용됨)
    @OneToOne(fetch = FetchType.LAZY)   //지연로딩 적용 -> Auth 앤티티 조회할때 user 객체는 불러오지 않음
    @JoinColumn(name = "user_id") //auth.getUser()에 실제로 접근할 때 User 쿼리 발생!
    private User user;

    public Auth( User user,String tokenType, String accessToken ,String refreshToken) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    public void updateAccessToken(String newAccessToken) {
        this.accessToken = newAccessToken;
    }

    public void updateRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }
}
