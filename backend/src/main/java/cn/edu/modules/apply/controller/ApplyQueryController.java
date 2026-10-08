package cn.edu.modules.apply.controller;

import cn.edu.common.LoginUser;
import cn.edu.common.Result;
import cn.edu.modules.apply.entity.Application;
import cn.edu.modules.apply.mapper.ApplicationMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 申请查询：学生看自己的 / 管理看全部 / 部门看本部门
 */
@RestController
@RequestMapping("/api/applies")
@RequiredArgsConstructor
public class ApplyQueryController {

    private final ApplicationMapper applicationMapper;

    @GetMapping
    public Result<Page<?>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) Long studentId) {

        // 学生强制只看自己，管理/部门可用参数过滤
        Long uid = LoginUser.getUid();
        Long filterId = LoginUser.isStudent() ? uid : studentId;

        var qw = new LambdaQueryWrapper<Application>()
                .eq(filterId != null, Application::getStudentId, filterId)
                .orderByDesc(Application::getId);

        return Result.ok(applicationMapper.selectPage(new Page<>(current, size), qw));
    }
}
