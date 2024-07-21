package com.study.event.api.event.controller;

import com.study.event.api.auth.TokenProvider;
import com.study.event.api.event.dto.request.EventUserSaveDto;
import com.study.event.api.event.dto.request.LoginRequestDto;
import com.study.event.api.event.dto.response.LoginResponseDto;
import com.study.event.api.event.service.EventUserService;
import com.study.event.api.exception.LoginFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

import static com.study.event.api.auth.TokenProvider.*;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class EventUserController {

    private final EventUserService eventUserService;

    // 이메일 중복확인 API
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(String email) {
        boolean isDuplicate = eventUserService.checkEmailDuplicate(email);

        return ResponseEntity.ok().body(isDuplicate);
    }

    // 인증 코드 검증 API
    @GetMapping("/code")
    public ResponseEntity<?> verifyCode(String email, String code) {
        log.info("{}'s verify code is [ {} ]", email, code);
        boolean isMatch = eventUserService.isMatchCode(email, code);

        return ResponseEntity.ok().body(isMatch);
    }

    // 회원가입 마무리 처리
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody EventUserSaveDto dto) {

        log.info("save User Info - {}", dto);
        try {
            eventUserService.confirmSignUp(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().body("saved success");
    }

    // 로그인 처리
    @PostMapping("/sign-in")
    public ResponseEntity<?> singIn(@RequestBody LoginRequestDto dto) {

        try {
            // 사용자가 회원가입시 입력한 정보(LoginRequestDto) 회원 로그인 인증
            // 로그인 정보가 맞다면(로그인 성공) 토큰 생성해서 LoginResponseDto에 성공 정보를 담아 반환
            LoginResponseDto responseDto = eventUserService.authenticate(dto);
            return ResponseEntity.ok().body(responseDto);
        } catch (LoginFailException e) {
            // service에서 예외발생 (로그인 실패)
            String errorMessage = e.getMessage();
            return ResponseEntity.status(422).body(errorMessage);
        }
    }

    // Premium회원으로 등급업하는 요청처리
    @PutMapping("/promote")
    public ResponseEntity<?> promote(
            @AuthenticationPrincipal TokenUserInfo userInfo // 로그인한 사용자의 토큰정보
    ) {

        try { // 토큰정보에 있는 userId를 받아서 등급업처리
            LoginResponseDto dto = eventUserService.promoteToPremium(userInfo.getUserId());
            return ResponseEntity.ok().body(dto); // 로그인 응답 정보 반환
        } catch (NoSuchElementException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}