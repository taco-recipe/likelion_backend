package org.example.backendproject.security.core;

import lombok.RequiredArgsConstructor;
import org.example.backendproject.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {


    // UserDetails <- 사용자 정보를 담는 인터페이스
    // 로그인한 사용자의 정보를 담아둠

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 유저의 권한을 반환하는 메서드
        // singleton 하나의 권한만 가짐
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
    }

    // 토큰에서 추출한 사용자 정보의 id를 반환 (테이블의 pk 값)
    //유저 엔티ㅣ티에서 id 추출
    public Long getId(){
        return user.getId();
    }


    @Override
    public String getPassword() {
        return user.getPassword(); // User 엔티티에서 password 반환
    }

    @Override
    public String getUsername() {
        return user.getUserid(); // User 엔티티에 userid 반환 중복되지 않는 식별 가능한 값
    }

    /** 아래는 현재 계정 상태를 판단하는 메서드 **/

    @Override // 현재 계정 상태 활성화인지 판단
    public boolean isEnabled() {

        return true;
    }

    @Override // 만룡ㅣㄴ지
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override // 잠겼는지
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override // 자격증명이 만료되지 않았는지
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
