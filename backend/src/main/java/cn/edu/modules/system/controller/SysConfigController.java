package cn.edu.modules.system.controller;

import cn.edu.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统配置：A12 薪资档 / 考勤阈值 / 课表开关
 * 直接读写 sys_config，新增配置无需改代码
 */
@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SysConfigController {

    private final JdbcTemplate jdbc;

    @GetMapping("/config")
    public Result<List<Map<String, Object>>> list() {
        return Result.ok(jdbc.queryForList("SELECT ckey, cval, remark FROM sys_config"));
    }

    @PostMapping("/config")
    public Result<?> save(@RequestBody Map<String, String> body) {
        jdbc.update("UPDATE sys_config SET cval=? WHERE ckey=?", body.get("cval"), body.get("ckey"));
        return Result.ok();
    }

    @GetMapping("/dict/{type}")
    public Result<?> dict(@PathVariable String type) {
        return Result.ok(jdbc.queryForList("SELECT dict_code, dict_label FROM sys_dict WHERE dict_type=?", type));
    }
}
