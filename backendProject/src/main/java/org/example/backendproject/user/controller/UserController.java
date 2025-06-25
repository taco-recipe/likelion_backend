package org.example.backendproject.user.controller;


import lombok.RequiredArgsConstructor;
import org.example.backendproject.security.core.CustomUserDetails;
import org.example.backendproject.user.dto.UserDTO;
import org.example.backendproject.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    //삭제
//    @Value("${PROJECT_NAME:web Server}")
//    private String instansName;
//
//    @GetMapping
//    public String test(){
//        return instansName;
//    }

    private final UserService userService;

    /* 내 정보 보기 */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getMyInfo(userDetails.getId()));
    }

    /* 유저 정보 수정 */
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateUser(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UserDTO dto) {
        UserDTO updated = userService.updateUser(userDetails.getId(), dto);
        return ResponseEntity.ok(updated);
    }
}
