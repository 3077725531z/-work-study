package cn.edu.modules.attend.controller;

import cn.edu.common.LoginUser;
import cn.edu.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 请假：P08 提交
 */
@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final JdbcTemplate jdbc;

    @PostMapping
    public Result<?> submit(@RequestBody Map<String, Object> b) {
        // 学生强制用登录 uid
        Object studentId = LoginUser.isStudent() ? LoginUser.getUid() : b.get("studentId");
        jdbc.update(
                "INSERT INTO leaves(student_id, job_id, type, start_date, end_date, reason, status) VALUES(?,?,?,?,?,?,?)",
                studentId, b.get("jobId"), b.getOrDefault("type", "事假"),
                b.get("startDate"), b.get("endDate"), b.getOrDefault("reason", ""), "待审批");
        return Result.ok();
    }
}
