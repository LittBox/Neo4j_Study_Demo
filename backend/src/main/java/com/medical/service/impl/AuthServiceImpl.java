package com.medical.service.impl;

import com.medical.dto.ChangePasswordDTO;
import com.medical.dto.LoginDTO;
import com.medical.dto.RegisterDTO;
import com.medical.entity.mysql.User;
import com.medical.exception.BusinessException;
import com.medical.repository.mysql.UserRepository;
import com.medical.service.AuthService;
import com.medical.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    @Transactional
    public String register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (registerDTO.getEmail() != null && !registerDTO.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(registerDTO.getEmail())) {
                throw new BusinessException(400, "邮箱已被注册");
            }
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickname(registerDTO.getNickname());
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        
        userRepository.save(user);
        
        log.info("用户注册成功：{}", registerDTO.getUsername());
        
        // 生成Token
        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }
    
    @Override
    public String login(LoginDTO loginDTO) {
        // 查找用户
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new BusinessException(400, "用户名或密码错误"));
        
        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        
        log.info("用户登录成功：{}", loginDTO.getUsername());
        
        // 生成Token
        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(400, "旧密码不正确");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        log.info("用户修改密码成功，userId={}", userId);
    }
}
