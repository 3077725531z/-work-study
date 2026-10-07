package cn.edu.modules.salary.controller;

import cn.edu.common.Result;
import cn.edu.modules.salary.mapper.SalaryMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 工资查询：A08 发放 / E08 复核列表
 */
@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryQueryController {

    private final SalaryMapper salaryMapper;

    @GetMapping("/list")
    public Result<Page<?>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) Long studentId) {

        var qw = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<cn.edu.modules.salary.entity.Salary>()
                .eq(studentId != null, cn.edu.modules.salary.entity.Salary::getStudentId, studentId)
                .orderByDesc(cn.edu.modules.salary.entity.Salary::getId);

        return Result.ok(salaryMapper.selectPage(new Page<>(current, size), qw));
    }
}
