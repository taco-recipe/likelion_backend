package org.example.backendproject.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  // ← @Controller → @RestController로 수정해야 JSON 응답 가능
@RequestMapping("/exception")
@RequiredArgsConstructor
public class ExceptionTestController {

    // 테스트용 런타임 예외
    @GetMapping("/runtime")
    public ResponseEntity<Void> throwRuntimeException() {
        throw new RuntimeException("테스트용 런타임 예외입니다.");
    }

    // 테스트용 인증 실패 예외
    @GetMapping("/unauthorized")
    public ResponseEntity<Void> throwUnauthorized() {
        throw new BadCredentialsException("인증 실패 테스트");
    }

    // 테스트용 권한 실패 예외
    @GetMapping("/forbidden")
    public ResponseEntity<Void> throwForbidden() {
        throw new AccessDeniedException("권한 없음 테스트");
    }

    // 테스트용 유효성 실패 예외 - 강제 시뮬레이션
    @GetMapping("/validation")
    public ResponseEntity<Void> throwValidation() {
        throw new MethodArgumentNotValidException(null, null);
    }

    // 테스트용 파라미터 타입 오류 예외
    @GetMapping("/bad-request")
    public ResponseEntity<Void> throwBadRequest() {
        throw new org.springframework.web.method.annotation.MethodArgumentTypeMismatchException(
                null, null, "testParam", null, new Throwable("잘못된 타입입니다.")
        );
    }


}