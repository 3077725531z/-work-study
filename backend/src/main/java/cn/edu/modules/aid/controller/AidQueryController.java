package cn.edu.modules.aid.controller;

import cn.edu.common.Result;
import cn.edu.modules.aid.mapper.AidMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认定查询：A06 复审列表
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

        var qw = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<cn.edu.modules.aid.entity.AidApplication>()
                .eq(studentId != null, cn.edu.modules.aid.entity.AidApplication::getStudentId, studentId)
                .orderByDesc(cn.edu.modules.aid.entity.AidApplication::getId);

        return Result.ok(aidMapper.selectPage(new Page<>(current, size), qw));
    }
}
