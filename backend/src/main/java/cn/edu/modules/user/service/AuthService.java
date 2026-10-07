package cn.edu.modules.user.service;

import cn.edu.common.BizException;
import cn.edu.common.JwtUtil;
import cn.edu.modules.user.dto.LoginRequest;
import cn.edu.modules.user.entity.User;
import cn.edu.modules.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 认证服务：三端统一登录
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;

    private final JwtUtil jwtUtil;

    public Map<String, Object> login(LoginRequest req) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getSno, req.getSno()));

        if (user == null) {
            throw new BizException(400, "账号不存在");
        }
        if (!user.getPasswordHash().equals(req.getPassword())) {
            throw new BizException(400, "密码错误");
        }

        String token = jwtUtil.sign(user.getId(), user.getRole());

        return Map.of(
                "token", token,
                "uid", user.getId(),
                "sno", user.getSno(),
                "role", user.getRole(),
                "name", user.getName());
    }
}
