package cn.edu.modules.aid.controller;

import cn.edu.common.LoginUser;
import cn.edu.common.Result;
import cn.edu.modules.aid.entity.AidApplication;
import cn.edu.modules.aid.mapper.AidMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认定查询：学生看自己的 / 管理看全部
 */
@RestController
@RequestMapping("/api/aid")
@RequiredArgsConstructor
public class AidQueryController {

    private final AidMapper aidMapper;

    @GetMapping("/list")
    public Result<Page<?>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) Long studentId) {

        Long filterId = LoginUser.isStudent() ? LoginUser.getUid() : studentId;

        var qw = new LambdaQueryWrapper<AidApplication>()
                .eq(filterId != null, AidApplication::getStudentId, filterId)
                .orderByDesc(AidApplication::getId);

        return Result.ok(aidMapper.selectPage(new Page<>(current, size), qw));
    }
}
