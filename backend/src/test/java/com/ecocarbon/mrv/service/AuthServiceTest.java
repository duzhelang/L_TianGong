package com.ecocarbon.mrv.service;

import com.ecocarbon.mrv.dto.LoginRequest;
import com.ecocarbon.mrv.entity.User;
import com.ecocarbon.mrv.repository.UserRepository;
import com.ecocarbon.mrv.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("认证服务测试")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setRole("ADMIN");
    }

    @Test
    @DisplayName("用户登录 - 成功")
    void login_Success() {
        LoginRequest request = new LoginRequest("admin", "123456");

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("123456", "$2a$10$encodedPassword"))
                .thenReturn(true);
        when(tokenProvider.generateAccessToken(1L, "admin", List.of("ADMIN")))
                .thenReturn("access-token");
        when(tokenProvider.generateRefreshToken(1L))
                .thenReturn("refresh-token");

        Map<String, Object> result = authService.login(request);

        assertNotNull(result);
        assertEquals("access-token", result.get("accessToken"));
        assertEquals("refresh-token", result.get("refreshToken"));
        assertEquals("admin", result.get("username"));
        assertEquals("ADMIN", result.get("role"));
    }

    @Test
    @DisplayName("用户登录 - 用户名不存在")
    void login_UserNotFound() {
        LoginRequest request = new LoginRequest("nonexistent", "123456");

        when(userRepository.findByUsername("nonexistent"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    @DisplayName("用户登录 - 密码错误")
    void login_WrongPassword() {
        LoginRequest request = new LoginRequest("admin", "wrongpassword");

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$encodedPassword"))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.login(request);
        });
    }

    @Test
    @DisplayName("用户注册 - 成功")
    void register_Success() {
        when(userRepository.findByUsername("newuser"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123"))
                .thenReturn("$2a$10$encodedNewPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        assertDoesNotThrow(() -> {
            authService.register("newuser", "password123", "USER");
        });

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("用户注册 - 用户名已存在")
    void register_UsernameExists() {
        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () -> {
            authService.register("admin", "password123", "USER");
        });
    }

    @Test
    @DisplayName("刷新Token - 成功")
    void refreshToken_Success() {
        String refreshToken = "valid-refresh-token";

        when(tokenProvider.validateToken(refreshToken))
                .thenReturn(true);
        when(tokenProvider.getTokenType(refreshToken))
                .thenReturn("refresh");
        when(tokenProvider.getUserId(refreshToken))
                .thenReturn(1L);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));
        when(tokenProvider.generateAccessToken(1L, "admin", List.of("ADMIN")))
                .thenReturn("new-access-token");

        Map<String, Object> result = authService.refreshToken(refreshToken);

        assertNotNull(result);
        assertEquals("new-access-token", result.get("accessToken"));
    }

    @Test
    @DisplayName("刷新Token - Token无效")
    void refreshToken_InvalidToken() {
        String refreshToken = "invalid-refresh-token";

        when(tokenProvider.validateToken(refreshToken))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.refreshToken(refreshToken);
        });
    }

    @Test
    @DisplayName("刷新Token - Token类型错误")
    void refreshToken_WrongTokenType() {
        String refreshToken = "access-token";

        when(tokenProvider.validateToken(refreshToken))
                .thenReturn(true);
        when(tokenProvider.getTokenType(refreshToken))
                .thenReturn("access");

        assertThrows(IllegalArgumentException.class, () -> {
            authService.refreshToken(refreshToken);
        });
    }
}
