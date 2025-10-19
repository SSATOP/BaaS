package com.baas.bank.account.controller;

import com.baas.bank.account.dto.UserDto;
import com.baas.bank.account.dto.UserLoginRequest;
import com.baas.bank.account.dto.UserLoginResponse;
import com.baas.bank.account.dto.UserSignupResponse;
import com.baas.bank.account.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    // 아이디 중복체크 API
    @GetMapping("/check-id")
    public ResponseEntity<String> checkId(@RequestParam("loginId") String loginId) {
        if (loginId == null || loginId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("아이디를 입력해주세요.");
        }

        try {
            boolean isDuplicate = userService.isLoginIdDuplicate(loginId.trim());
            
            if (isDuplicate) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 아이디입니다.");
            } else {
                return ResponseEntity.ok("사용 가능한 아이디입니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // 이메일 중복체크 API
    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam("email") String email) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("이메일을 입력해주세요.");
        }

        try {
            boolean isDuplicate = userService.isEmailDuplicate(email.trim());
            
            if (isDuplicate) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 이메일입니다.");
            } else {
                return ResponseEntity.ok("사용 가능한 이메일입니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponse> signupSubmit(@RequestBody UserDto userDto){
        // 예외가 발생하면 GlobalExceptionHandler가 처리
        userService.registUser(userDto);
        
        // 등록 성공 시 등록된 사용자 정보 조회 (비밀번호 제외)
        UserDto savedUser = userService.findByLoginIdWithoutPassword(userDto.getLoginId());
        UserSignupResponse response = new UserSignupResponse(savedUser.getId(), savedUser.getLoginId(), savedUser.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
