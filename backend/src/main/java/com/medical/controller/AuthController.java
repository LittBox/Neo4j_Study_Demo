package com.medical.controller;

import com.medical.common.Result;
import com.medical.dto.ChangePasswordDTO;
import com.medical.dto.LoginDTO;
import com.medical.dto.RegisterDTO;
import com.medical.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Map<String, String>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        String token = authService.register(registerDTO);
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        return Result.success("注册成功", data);
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginDTO loginDTO) {
        String token = authService.login(loginDTO);
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        return Result.success("登录成功", data);
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<?> changePassword(@Valid @RequestBody ChangePasswordDTO dto,
                                    HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }
        authService.changePassword(userId, dto);
        return Result.success();
    }
}
