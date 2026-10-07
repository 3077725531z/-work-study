package cn.edu.modules.apply.controller;

import cn.edu.common.Result;
import cn.edu.modules.apply.dto.ApplyRequest;
import cn.edu.modules.apply.entity.Application;
import cn.edu.modules.apply.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 申请：提交（含课表拦截）/ 我的申请 / 部门审核
 * 对应 P05 P06 E05 A05
 */
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplyService applyService;

    @PostMapping
    public Result<Application> submit(@Validated @RequestBody ApplyRequest req) {
        return Result.ok(applyService.submit(req));
    }

    @GetMapping
    public Result<List<Application>> myList(@RequestParam Long studentId) {
        return Result.ok(applyService.myList(studentId));
    }

    @PostMapping("/{id}:audit")
    public Result<Application> audit(
            @PathVariable Long id,
            @RequestParam boolean pass,
            @RequestParam(required = false) String comment) {

        return Result.ok(applyService.audit(id, pass, comment));
    }

    @GetMapping("/conflict-tip")
    public Result<Map<String, String>> tip() {
        return Result.ok(Map.of(
                "rule", "所选时段与课表交叉即冲突，冲突时返回 403",
                "example", "周三晚与《数据库》(周三晚)冲突"));
    }
}
