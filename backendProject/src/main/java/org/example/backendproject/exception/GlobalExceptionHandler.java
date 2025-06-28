package org.example.backendproject.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // 모든 컨트롤러에 대한 전역 예외 처리 클래스
public class GlobalExceptionHandler {

    // 커스텀 에러 응답 객체
    private ResponseEntity<ErrorResponse> buildErrorResponse(int code, String message, String detail, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(code, message, detail);
        return ResponseEntity.status(status).body(errorResponse);
    }

    // ==============================
    // 1. 일반 런타임 예외
    // ==============================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("[RuntimeException] {}", e.getMessage(), e);
        return buildErrorResponse(2000, "런타임 오류 발생", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // ==============================
    // 2. 잘못된 요청: 타입, 파싱, 파라미터 누락 등
    // ==============================
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
        log.warn("[BadRequest] {}", e.getMessage());
        return buildErrorResponse(4000, "잘못된 요청입니다", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // ==============================
    // 3. DTO 유효성 검증 실패
    // ==============================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((m1, m2) -> m1 + ", " + m2)
                .orElse("유효성 검사 실패");
        log.warn("[ValidationFail] {}", msg);
        return buildErrorResponse(4001, "입력값 검증 실패", msg, HttpStatus.BAD_REQUEST);
    }

    // ==============================
    // 4. 인증 실패 (아이디/비번 틀림)
    // ==============================
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException e) {
        log.warn("[AuthenticationFail] {}", e.getMessage());
        return buildErrorResponse(4010, "인증 실패", "아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
    }

    // ==============================
    // 5. 인가 실패 (권한 없음)
    // ==============================
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        log.warn("[AuthorizationFail] {}", e.getMessage());
        return buildErrorResponse(4030, "접근 권한 없음", "해당 리소스에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }

    // ==============================
    // 6. 처리되지 않은 모든 예외
    // ==============================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandledException(Exception e) {
        log.error("[UnhandledException]", e);
        return buildErrorResponse(5000, "서버 오류", "예상치 못한 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}