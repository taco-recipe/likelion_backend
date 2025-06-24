package org.example.backendproject.user.service;

import lombok.RequiredArgsConstructor;
import org.example.backendproject.user.dto.UserDTO;
import org.example.backendproject.user.dto.UserProfileDTO;
import org.example.backendproject.user.entity.User;
import org.example.backendproject.user.entity.UserProfile;
import org.example.backendproject.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDTO getMyInfo(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. "));


        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserid(user.getUserid());

        UserProfile profile = user.getUserProfile();

        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setUsername(profile.getUsername());
        profileDTO.setEmail(profile.getEmail());
        profileDTO.setPhone(profile.getPhone());
        profileDTO.setAddress(profile.getAddress());
        dto.setProfile(profileDTO);

        return dto;
    }
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO)  {
        User user = userRepository.findById(id)  //유저 레포지토리를 통해서 유저를 가져옴
                .orElseThrow(()-> new IllegalArgumentException("회원이 존재하지 않습니다. "));

        UserProfile profile = user.getUserProfile(); //프로필 객체를 만들어서 기존에 변경되기 전 프로필을 넣어줍니다.

        if (profile != null && userDTO.getProfile() != null) { //수정하려는 프로필이 있는지 체크 수정하려는 프로필정보가 있는지 체크
            UserProfileDTO dtoProfile = userDTO.getProfile();

            //프로필을  수정하기 위해 전달받은 데이터로 변경합니다.
            if (dtoProfile.getUsername() != null) profile.setUsername(dtoProfile.getUsername());
            if (dtoProfile.getEmail() != null) profile.setEmail(dtoProfile.getEmail());
            if (dtoProfile.getPhone() != null) profile.setPhone(dtoProfile.getPhone());
            if (dtoProfile.getAddress() != null) profile.setAddress(dtoProfile.getAddress());

        }

        //아래는 변경된 내용을 프론트에 던져주기 위해 생성합니다.
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserid(user.getUserid());
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setUsername(profile.getUsername());
        profileDTO.setEmail(profile.getEmail());
        profileDTO.setPhone(profile.getPhone());
        profileDTO.setAddress(profile.getAddress());
        dto.setProfile(profileDTO);
        return dto;
    }
}
