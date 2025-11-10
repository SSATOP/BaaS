package com.baas.oauth.controller;

import com.baas.oauth.dto.RegisterDTO;
import com.baas.oauth.model.Register;
import com.baas.oauth.service.RegisterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RegisterController {
    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @GetMapping("/register")
    public String registerPage() {

        return "register";
    }

    @PostMapping("/register")
    @ResponseBody
    public Register register(RegisterDTO dto) {

        return registerService.register(dto);
    }
}
