package cn.edu.modules.user.controller;

import cn.edu.common.BizException;
import cn.edu.common.LoginUser;
import cn.edu.common.Result;
import cn.edu.modules.user.entity.User;
import cn.edu.modules.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理：A04 学生管理 / 部门账号
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    @GetMapping
    public Result<Page<?>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size,
            String role) {

        var qw = new LambdaQueryWrapper<User>()
                .eq(role != null, User::getRole, role)
                .orderByDesc(User::getId);

        return Result.ok(userMapper.selectPage(new Page<>(current, size), qw));
    }

    /**
     * 修改密码：校验旧密码后更新
     */
    @PutMapping("/{id}/password")
    public Result<Map<String, Object>> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String oldPwd = body.get("oldPassword");
        String newPwd = body.get("newPassword");

        // 学生只能改自己的密码
        if (LoginUser.isStudent() && !id.equals(LoginUser.getUid())) {
            throw new BizException(403, "无权修改他人密码");
        }

        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        if (oldPwd == null || !oldPwd.equals(user.getPasswordHash())) {
            throw new BizException(400, "旧密码错误");
        }
        if (newPwd == null || newPwd.length() < 6 || newPwd.length() > 20) {
            throw new BizException(400, "新密码须为6–20位");
        }

        user.setPasswordHash(newPwd);
        userMapper.updateById(user);

        return Result.ok(Map.of("changed", true));
    }
}
