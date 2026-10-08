package cn.edu.modules.salary.controller;

import cn.edu.common.BizException;
import cn.edu.common.LoginUser;
import cn.edu.common.Result;
import cn.edu.modules.salary.entity.Salary;
import cn.edu.modules.salary.mapper.SalaryMapper;
import cn.edu.modules.salary.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 工资：核算 / 复核 / 发放
 * 对应 P09 P10 E08 A08
 */
@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    private final SalaryMapper salaryMapper;

    @PostMapping("/calc")
    public Result<Map<String, BigDecimal>> calc(@RequestBody Map<String, Object> body) {
        BigDecimal hours = new BigDecimal(body.get("hours").toString());
        BigDecimal rate = new BigDecimal(body.get("rate").toString());
        Object deduct = body.getOrDefault("deduct", "0");

        return Result.ok(salaryService.calc(hours, rate, new BigDecimal(deduct.toString())));
    }

    @PostMapping("/{id}:confirm")
    public Result<?> confirm(@PathVariable Long id) {
        Salary s = salaryMapper.selectById(id);
        if (s == null) throw new BizException(404, "工资记录不存在");
        // 学生只能确认自己的工资
        if (LoginUser.isStudent() && !s.getStudentId().equals(LoginUser.getUid())) {
            throw new BizException(403, "无权操作他人工资");
        }
        s.setStatus("已复核");
        salaryMapper.updateById(s);
        return Result.ok();
    }

    @PostMapping("/{id}:pay")
    public Result<?> pay(@PathVariable Long id) {
        var s = salaryMapper.selectById(id);
        s.setStatus("已发放");
        salaryMapper.updateById(s);
        return Result.ok();
    }
}
