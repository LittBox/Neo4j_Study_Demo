package com.medical.service;

import com.medical.dto.LoginDTO;
import com.medical.dto.RegisterDTO;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户注册
     */
    String register(RegisterDTO registerDTO);
    
    /**
     * 用户登录
     */
    String login(LoginDTO loginDTO);
}
