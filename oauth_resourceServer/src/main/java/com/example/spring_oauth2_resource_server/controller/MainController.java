package com.example.spring_oauth2_resource_server.controller;

import com.example.spring_oauth2_resource_server.dto.ResDTO;
import com.example.spring_oauth2_resource_server.dto.ResWrapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MainController {
    @RequestMapping(value = "/me", method = {RequestMethod.GET, RequestMethod.POST})
    public ResWrapper me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        // JWT에서 scope 추출
        Collection<String> scopes = extractScopes(jwt);
        
        ResDTO resDTO = new ResDTO();
        
        // scope 기반으로 사용자 정보 추출
        // profile scope가 있으면 name, gender 정보 제공
        if (scopes.contains("profile")) {
            if (jwt.hasClaim("name")) {
                resDTO.setUsername(jwt.getClaimAsString("name"));
            } else {
                resDTO.setUsername(authentication.getName());
            }
            if (jwt.hasClaim("gender")) {
                resDTO.setGender(jwt.getClaimAsString("gender"));
            }
        } else {
            // profile scope가 없어도 기본 username은 제공
            resDTO.setUsername(authentication.getName());
        }
        
        // email scope가 있으면 이메일 정보 제공
        if (scopes.contains("email") && jwt.hasClaim("email")) {
            resDTO.setEmail(jwt.getClaimAsString("email"));
        }
        
        // phone scope가 있으면 전화번호 정보 제공
        if (scopes.contains("phone") && jwt.hasClaim("phone_number")) {
            resDTO.setPhoneNumber(jwt.getClaimAsString("phone_number"));
        }

        ResWrapper resWrapper = new ResWrapper();
        resWrapper.setResponse(resDTO);

        return resWrapper;
    }
    
    private Collection<String> extractScopes(Jwt jwt) {
        if (jwt.hasClaim("scope")) {
            Object scopeClaim = jwt.getClaim("scope");
            if (scopeClaim instanceof String) {
                // scope가 공백으로 구분된 문자열인 경우
                return List.of(((String) scopeClaim).split(" "));
            } else if (scopeClaim instanceof List) {
                // scope가 리스트인 경우
                return ((List<?>) scopeClaim).stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
            } else if (scopeClaim instanceof Collection) {
                // scope가 Collection인 경우
                return ((Collection<?>) scopeClaim).stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
            }
        }
        // scope 클레임이 없거나 알 수 없는 형식인 경우 빈 리스트 반환
        return List.of();
    }
}
