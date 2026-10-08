package cn.edu.modules.timetable.controller;

import cn.edu.common.LoginUser;
import cn.edu.common.Result;
import cn.edu.modules.timetable.entity.Timetable;
import cn.edu.modules.timetable.mapper.TimetableMapper;
import cn.edu.modules.timetable.service.TimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课表：P15，我的课表
 * 同步接口预留教务对接位，正式替换为 HttpClient 调用
 */
@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableMapper timetableMapper;

    private final TimetableService timetableService;

    @GetMapping
    public Result<List<Timetable>> list(
            @RequestParam(required = false) Long studentId,
            @RequestParam(defaultValue = "2026-秋") String semester) {

        // 学生强制用自己的 uid
        Long uid = LoginUser.isStudent() ? LoginUser.getUid() : studentId;
        return Result.ok(timetableService.listOf(uid, semester));
    }

    @PostMapping
    public Result<Timetable> save(@RequestBody Timetable t) {
        // 学生强制写入自己的 uid
        if (LoginUser.isStudent()) {
            t.setStudentId(LoginUser.getUid());
        }
        timetableMapper.insert(t);
        return Result.ok(t);
    }

    @PostMapping("/sync")
    public Result<Map<String, Object>> sync(@RequestParam(required = false) Long studentId) {
        Long uid = LoginUser.isStudent() ? LoginUser.getUid() : studentId;
        return Result.ok(Map.of("synced", 6, "semester", "2026-秋", "studentId", uid));
    }
}
