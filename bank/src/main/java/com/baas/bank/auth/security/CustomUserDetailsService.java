package com.baas.bank.auth.security;

import com.baas.bank.user.dto.UserDto;
import com.baas.bank.user.dao.UserDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto user = userDAO.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with loginId: " + username));
        
        return new CustomUserDetails(user);
    }
}
