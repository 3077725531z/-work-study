package cn.edu.modules.user.controller;

import cn.edu.common.Result;
import cn.edu.modules.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

        var qw = new LambdaQueryWrapper<cn.edu.modules.user.entity.User>()
                .eq(role != null, cn.edu.modules.user.entity.User::getRole, role)
                .orderByDesc(cn.edu.modules.user.entity.User::getId);

        return Result.ok(userMapper.selectPage(new Page<>(current, size), qw));
    }
}
