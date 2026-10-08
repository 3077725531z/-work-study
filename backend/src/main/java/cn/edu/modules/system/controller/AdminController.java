package cn.edu.modules.system.controller;

import cn.edu.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端总览 + 统计
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final JdbcTemplate jdbc;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> m = new HashMap<>();
        // KPI
        m.put("jobs", count("SELECT COUNT(*) FROM jobs WHERE status='招募中'"));
        m.put("onboard", count("SELECT COUNT(DISTINCT student_id) FROM applications WHERE status='已通过'"));
        long todoJobs = count("SELECT COUNT(*) FROM jobs WHERE status='待审核'");
        long todoApplies = count("SELECT COUNT(*) FROM applications WHERE status='待审核'");
        long todoSalary = count("SELECT COUNT(*) FROM salaries WHERE status='待复核' OR status='已复核'");
        long todoAppeals = count("SELECT COUNT(*) FROM appeals WHERE status='待处理'");
        m.put("todos", todoJobs + todoApplies + todoSalary + todoAppeals);
        // 本月应发（month 是字符串，用 LEFT 截取年月匹配）
        m.put("payMonth", countDouble("SELECT COALESCE(SUM(net),0) FROM salaries WHERE LEFT(month,7)=DATE_FORMAT(CURDATE(),'%Y-%m')"));
        // 出勤率
        long checked = count("SELECT COUNT(*) FROM attendances WHERE work_date = CURDATE() AND status='正常'");
        long total = count("SELECT COUNT(DISTINCT student_id) FROM applications WHERE status='已通过'");
        m.put("rate", total == 0 ? 0 : Math.round(checked * 100.0 / total));

        // 待办清单
        Map<String, Long> todoList = new HashMap<>();
        todoList.put("jobs", todoJobs);
        todoList.put("applies", todoApplies);
        todoList.put("salary", todoSalary);
        todoList.put("appeals", todoAppeals);
        m.put("todoList", todoList);

        // 预警：缺勤>=2次的学生
        m.put("warnings", jdbc.queryForList(
                "SELECT s.name, COUNT(*) AS cnt FROM attendances a " +
                "JOIN users s ON s.id=a.student_id " +
                "WHERE a.status <> '正常' " +
                "GROUP BY a.student_id, s.name HAVING cnt >= 2 LIMIT 5"));
        return Result.ok(m);
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        // 近 6 个月工资发放总额（month 直接是字符串如 2026-10）
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT month AS m, COALESCE(SUM(net), 0) AS amt " +
                "FROM salaries WHERE month IS NOT NULL AND month <> '' " +
                "GROUP BY month " +
                "ORDER BY month DESC LIMIT 6");
        java.util.Collections.reverse(rows);
        String[] months = rows.stream().map(r -> String.valueOf(r.get("m"))).toArray(String[]::new);
        Double[] amounts = rows.stream().map(r -> ((Number) r.get("amt")).doubleValue()).toArray(Double[]::new);
        Map<String, Object> m = new HashMap<>();
        m.put("months", months);
        m.put("amounts", amounts);
        return Result.ok(m);
    }

    private long count(String sql) {
        try {
            Long v = jdbc.queryForObject(sql, Long.class);
            return v == null ? 0 : v;
        } catch (Exception e) {
            return 0;
        }
    }

    private double countDouble(String sql) {
        try {
            Double v = jdbc.queryForObject(sql, Double.class);
            return v == null ? 0 : v;
        } catch (Exception e) {
            return 0;
        }
    }
}
