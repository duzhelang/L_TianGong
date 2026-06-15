package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.dto.LoginRequest;
import com.ecocarbon.mrv.entity.User;
import com.ecocarbon.mrv.repository.UserRepository;
import com.ecocarbon.mrv.security.JwtTokenProvider;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .filter(item -> passwordEncoder.matches(request.password(), item.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));
        String accessToken = tokenProvider.generateAccessToken(user.getId(), user.getUsername(), List.of(user.getRole()));
        String refreshToken = tokenProvider.generateRefreshToken(user.getId());
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "username", user.getUsername(),
                "role", user.getRole()
        );
    }

    public Map<String, Object> refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token无效或已过期");
        }
        String tokenType = tokenProvider.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new IllegalArgumentException("Token类型错误");
        }
        Long userId = tokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        String newAccessToken = tokenProvider.generateAccessToken(user.getId(), user.getUsername(), List.of(user.getRole()));
        return Map.of("accessToken", newAccessToken);
    }

    public void register(String username, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        userRepository.save(user);
    }
}
