package org.example.backendproject.security.core;


import lombok.RequiredArgsConstructor;
import org.example.backendproject.user.entity.User;
import org.example.backendproject.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;

    //로그인할 때 스프링에서 DB 현재 로그인하는 사용자가 존재하고 있는지 확인하는 메서드
    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        User user = userRepository.findByUserid(userid)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found with userid : " + userid));
        return new CustomUserDetails(user);
    }

    public UserDetails loadUserById(Long Id) throws UsernameNotFoundException {
        User user = userRepository.findById(Id)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found with userid : " + Id));
        return new CustomUserDetails(user);
    }


}
