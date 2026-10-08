package cn.edu.modules.salary.controller;

import cn.edu.common.LoginUser;
import cn.edu.common.Result;
import cn.edu.modules.salary.entity.Salary;
import cn.edu.modules.salary.mapper.SalaryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 工资查询：学生看自己的 / 管理看全部
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

        Long filterId = LoginUser.isStudent() ? LoginUser.getUid() : studentId;

        var qw = new LambdaQueryWrapper<Salary>()
                .eq(filterId != null, Salary::getStudentId, filterId)
                .orderByDesc(Salary::getId);

        return Result.ok(salaryMapper.selectPage(new Page<>(current, size), qw));
    }
}
