package org.example.backendproject.Auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backendproject.Auth.dto.LoginReponseDTO;
import org.example.backendproject.Auth.dto.LoginRequestDTO;
import org.example.backendproject.Auth.dto.SignUpRequestDTO;
import org.example.backendproject.Auth.entity.Auth;
import org.example.backendproject.Auth.repository.AuthRepository;
import org.example.backendproject.security.core.CustomUserDetails;
import org.example.backendproject.security.core.Role;
import org.example.backendproject.security.jwt.JwtTokenProvider;
import org.example.backendproject.user.dto.UserDTO;
import org.example.backendproject.user.dto.UserProfileDTO;
import org.example.backendproject.user.entity.User;
import org.example.backendproject.user.entity.UserProfile;
import org.example.backendproject.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.accessTokenExpirationTime}")
    private Long jwtAccessTokenExpirationTime;
    @Value("${jwt.refreshTokenExpirationTime}")
    private Long jwtRefreshTokenExpirationTime;


    //회원가입
    @Transactional
    public void signUp(SignUpRequestDTO dto){

        log.info(String.valueOf(dto));

        if (userRepository.findByUserid(dto.getUserid()).isPresent()){
            throw new RuntimeException("사용자가 이미 존재합나다.");
        }

        User user = new User();
        user.setUserid(dto.getUserid());
        //user.setPassword(dto.getPassword());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));// 비밀번호 암호화 저장
        log.info(user.getPassword());
        //+
        user.setRole(Role.ROLE_USER);

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


//    //로그인
//    public LoginReponseDTO login(LoginRequestDTO loginRequestDTO) {
//        User user = userRepository.findByUserid(loginRequestDTO.getUserid())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if(!loginRequestDTO.getPassword().equals(user.getPassword())) {
//            throw new RuntimeException("Wrong password");
//        }
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.setId(user.getId());
//        userDTO.setUserid(user.getUserid());
//
//        UserProfileDTO userProfileDTO = new UserProfileDTO();
//        userProfileDTO.setUsername(user.getUserProfile().getUsername());
//        userProfileDTO.setEmail(user.getUserProfile().getEmail());
//        userProfileDTO.setPhone(user.getUserProfile().getPhone());
//        userProfileDTO.setAddress(user.getUserProfile().getAddress());
//
//        userDTO.setProfile(userProfileDTO);
//
//        return userDTO;
//
//    }

    //로그인
    public LoginReponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByUserid(loginRequestDTO.getUserid())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //입력한 비밀번호가 암호화된 비밀번호와 일치하는지 확인
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())){
            throw  new BadCredentialsException("Wrong password");
            //시큐리티 로그인 과정에서 비밀번호가 일치하지 않으면 던저주는 예외
        }
        // 위 비밀번호가 일치하면 기존 토큰 정보를 비교하고 토큰이 있으면 업ㅂ데이트 없으면 새로 발급
        String accessToken = jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(user)
                ,user.getPassword())
                ,jwtAccessTokenExpirationTime);

        String refreshToken = jwtTokenProvider.generateToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(user)
                        ,user.getPassword())
                ,jwtRefreshTokenExpirationTime);

        //현재 로그인한 시점에 db에 있는지 확인 후 토큰을 db에 저장 후 로그인 처리
        if (authRepository.existsByUser(user)){
            Auth auth = user.getAuth();
            auth.setRefreshToken(refreshToken);
            auth.setAccessToken(accessToken);
            authRepository.save(auth);

            return new LoginReponseDTO(auth);
        }
        // 위에 DB에 사용자 정보가 없으면 새로 생성해서 로그인 처리
        Auth auth = new Auth(user,"Bearer ",accessToken,refreshToken);
        authRepository.save(auth);
        return new LoginReponseDTO(auth);



//        UserDTO userDTO = new UserDTO();
//        userDTO.setId(user.getId());
//        userDTO.setUserid(user.getUserid());
//
//        UserProfileDTO userProfileDTO = new UserProfileDTO();
//        userProfileDTO.setUsername(user.getUserProfile().getUsername());
//        userProfileDTO.setEmail(user.getUserProfile().getEmail());
//        userProfileDTO.setPhone(user.getUserProfile().getPhone());
//        userProfileDTO.setAddress(user.getUserProfile().getAddress());
//
//        userDTO.setProfile(userProfileDTO);
//
//        return userDTO;

    }


    //리프레시 토큰ㅇ르 받아서 새로운 액세스 토큰을 발급해주는 서비스
    @Transactional
    public String refreshToken(String refreshToken) {
        //리프래시 토큰 유효성 검사
        if (jwtTokenProvider.validateToken(refreshToken)){
            //DB에서 리프레시 토큰을 가진 사용자가 있는지 확인
            Auth auth = authRepository.findByRefreshToken(refreshToken).orElseThrow(
                    () -> new RuntimeException("Refresh token not found \nRefresh token: " + refreshToken));

            //있으면 인증 객체를 만들어 새로운 토큰 발급
            String newAccessToken = jwtTokenProvider.generateToken(
                    new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(auth.getUser()),
                            auth.getUser().getPassword())
                    ,jwtAccessTokenExpirationTime  // 엑세스 토큰 만료 시간으로 설정
                    );
            auth.updateAccessToken(newAccessToken); //토큰 업데이트
            authRepository.save(auth); // DB에 반영

            return newAccessToken;
        }
        else {
            throw new IllegalArgumentException("Invalid refresh token");
        }
    }

}
