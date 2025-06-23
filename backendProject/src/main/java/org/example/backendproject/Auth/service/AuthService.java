package org.example.backendproject.Auth.service;

import lombok.RequiredArgsConstructor;
import org.example.backendproject.Auth.dto.LoginRequestDTO;
import org.example.backendproject.Auth.dto.SignUpRequestDTO;
import org.example.backendproject.Auth.entity.Auth;
import org.example.backendproject.Auth.repository.AuthRepository;
import org.example.backendproject.user.dto.UserDTO;
import org.example.backendproject.user.entity.User;
import org.example.backendproject.user.entity.UserProfile;
import org.example.backendproject.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;


    @Transactional
    public void signUp(SignUpRequestDTO dto){

        System.out.println(dto);

        if (userRepository.findByUserid(dto.getUserid()).isPresent()){
            throw new RuntimeException("사용자가 이미 존재합나다.");
        }

        User user = new User();
        user.setUserid(dto.getUserid());
        user.setPassword(dto.getPassword());

        UserProfile profile = new UserProfile();
        profile.setUsername(dto.getUsername());
        profile.setEmail(dto.getEmail());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());

        /** 연관관계 설정 **/
        profile.setUser(user);
        user.setUserProfile(profile);

        userRepository.save(user);

    }


    public UserDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByUserid(loginRequestDTO.getUserid())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(!loginRequestDTO.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUserid(user.getUserid());

        userDTO.setUsername(user.getUserProfile().getUsername());
        userDTO.setEmail(user.getUserProfile().getEmail());
        userDTO.setPhone(user.getUserProfile().getPhone());
        userDTO.setAddress(user.getUserProfile().getAddress());

        return userDTO;

    }

}
