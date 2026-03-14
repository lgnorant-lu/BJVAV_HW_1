package com.example.demo.auth;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 简化处理：实际应使用 BCrypt 加密存储和校验
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtils.generateToken(user);
        return Map.of(
                "token", token,
                "username", user.getUsername(),
                "role", user.getRole().name(),
                "userId", user.getId()
        );
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        // 默认注册为患者
        if (user.getRole() == null) {
            user.setRole(User.Role.PATIENT);
        }
        return userRepository.save(user);
    }
}
