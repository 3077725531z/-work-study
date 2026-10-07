package cn.edu.modules.user.controller;

import cn.edu.common.BizException;
import cn.edu.common.Result;
import cn.edu.modules.user.dto.LoginRequest;
import cn.edu.modules.user.entity.User;
import cn.edu.modules.user.mapper.UserMapper;
import cn.edu.modules.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证：P01 学生登录 / E01 部门登录 / A01 管理登录
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final UserMapper userMapper;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginRequest req) {
        return Result.ok(authService.login(req));
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody Map<String, Object> body) {
        String sno = String.valueOf(body.getOrDefault("sno", "")).trim();
        String password = String.valueOf(body.getOrDefault("password", ""));

        if (!sno.matches("^[A-Za-z0-9]{6,20}$")) {
            throw new BizException(400, "学号为6–20位字母或数字");
        }
        if (password.length() < 6 || password.length() > 20) {
            throw new BizException(400, "密码为6–20位");
        }

        User exists = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getSno, sno));
        if (exists != null) {
            throw new BizException(400, "该学号已注册");
        }

        User user = new User();
        user.setSno(sno);
        user.setPasswordHash(password);
        user.setRole("student");
        user.setName(String.valueOf(body.getOrDefault("name", "")));
        user.setCollege(String.valueOf(body.getOrDefault("college", "")));
        user.setPhone(String.valueOf(body.getOrDefault("phone", "")));
        user.setStatus("待审核");
        userMapper.insert(user);
        user.setPasswordHash(null);
        return Result.ok(user);
    }
}
