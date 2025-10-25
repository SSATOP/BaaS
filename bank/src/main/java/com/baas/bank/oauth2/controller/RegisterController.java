package com.baas.bank.oauth2.controller;

import com.baas.bank.oauth2.entity.RegisterEntity;
import com.baas.bank.oauth2.service.RegisterService;
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
    public RegisterEntity register(RegisterEntity dto) {

        return registerService.register(dto);
    }
}