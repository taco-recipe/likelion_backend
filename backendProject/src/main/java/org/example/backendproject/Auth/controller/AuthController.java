package org.example.backendproject.Auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.backendproject.Auth.dto.LoginReponseDTO;
import org.example.backendproject.Auth.dto.LoginRequestDTO;
import org.example.backendproject.Auth.dto.SignUpRequestDTO;
import org.example.backendproject.Auth.service.AuthService;
import org.example.backendproject.user.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;


    /* 회원가입 */
    @PostMapping("/signUp")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequestDTO signUpRequestDTO) {

        try {
            authService.signUp(signUpRequestDTO);
            return ResponseEntity.ok("Sign up successful");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

//    @PostMapping("/login")
//    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
//        try{
//
//            UserDTO loginUser = authService.login(loginRequestDTO);
//
//            log.info("로그인 성공 = "+new ObjectMapper().writeValueAsString(loginUser));
//
//            return ResponseEntity.ok(loginUser);
//        }
//        catch (Exception e){
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }

    @PostMapping("/loginSecurity")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginReponseDTO loginReponseDTO = authService.login(loginRequestDTO);

        return ResponseEntity.ok(loginReponseDTO);
    }


    /** 토큰 갱신 API**/
    //refresh http 요청 헤더에서 토큰을 추출하고 그 토큰으로 리프래시 토큰을 발급
    @PostMapping("/refresh")
    public ResponseEntity<?>  refreshToken(@RequestHeader(value = "Authorization",required = false) String authorizationationHearder,
                                           HttpServletRequest request) {

        String refreshToken = null;
        //1. 쿠키에서 찾기
        if (request.getCookies() != null){
            for (Cookie cookie : request.getCookies()){
                if ("refreshToken".equals(cookie.getName())){
                    refreshToken = cookie.getValue();
                }
            }
        }

        //2. Authorization 헤더 찾기
        if (refreshToken == null && authorizationationHearder != null && authorizationationHearder.startsWith("Bearer ")){
            refreshToken = authorizationationHearder.replace("Bearer ", "").trim();
        }
        if (refreshToken == null || refreshToken.isEmpty()){
            throw new IllegalArgumentException("Refresh token is null or empty");
        }

        String newAccessToken = authService.refreshToken(refreshToken);
        //json 객체로 면환하여 front에 내려주기
        Map<String, String> res = new HashMap<>();
        res.put("accessToken", newAccessToken);
        res.put("refreshToken", refreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(res);

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        // accessToken 쿠키 삭제
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0); // 즉시 만료!

        // refreshToken 쿠키 삭제
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);

        // 응답에 쿠키 삭제 포함
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        // (추가) 서버 세션도 있다면 만료
        // request.getSession().invalidate();

        return ResponseEntity.ok().body("로그아웃 완료 (쿠키 삭제됨)");
    }

}
