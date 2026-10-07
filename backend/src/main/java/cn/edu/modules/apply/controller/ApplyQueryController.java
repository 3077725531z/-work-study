package cn.edu.modules.apply.controller;

import cn.edu.common.Result;
import cn.edu.modules.apply.mapper.ApplicationMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 申请查询：A05 抽查 / E05 部门审核列表
 */
@RestController
@RequestMapping("/api/applies")
@RequiredArgsConstructor
public class ApplyQueryController {

    private final ApplicationMapper applicationMapper;

    @GetMapping
    public Result<Page<?>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size) {

        return Result.ok(applicationMapper.selectPage(new Page<>(current, size), null));
    }
}
