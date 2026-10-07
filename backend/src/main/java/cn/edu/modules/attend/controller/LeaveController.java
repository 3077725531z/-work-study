package cn.edu.modules.attend.controller;

import cn.edu.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 请假：P08 提交（演示可直接写入 leaves 表）
 */
@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final JdbcTemplate jdbc;

    @PostMapping
    public Result<?> submit(@RequestBody Map<String, Object> b) {
        jdbc.update(
                "INSERT INTO leaves(student_id, job_id, type, start_date, end_date, reason, status) VALUES(?,?,?,?,?,?,?)",
                b.get("studentId"), b.get("jobId"), b.getOrDefault("type", "事假"),
                b.get("startDate"), b.get("endDate"), b.getOrDefault("reason", ""), "待审批");
        return Result.ok();
    }
}
