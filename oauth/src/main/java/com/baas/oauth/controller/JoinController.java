package com.baas.oauth.controller;

import com.baas.oauth.dto.UserDTO;
import com.baas.oauth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class JoinController {
    private final UserService userService;

    /**
     * 회원가입 페이지(HTML)를 보여줍니다.
     *
     * @return "join" (HTML 파일명)
     */
    @GetMapping("/join")
    public String joinPage() {
        // "join.html" 템플릿 파일을 반환 (e.g., /resources/templates/join.html)
        return "joinPage";
    }

    /**
     * 회원가입 폼 제출(POST)을 처리합니다.
     *
     * @param dto
     * @return "redirect:/login" (회원가입 성공 시 로그인 페이지로 이동)
     */
    @PostMapping("/join")
    public String joinProcess(UserDTO dto) {
        log.info(dto.toString());
        // 서비스 로직 호출
        userService.join(dto);

        // 회원가입 성공 후 로그인 페이지로 리다이렉트
        return "redirect:/login";
    }
}
