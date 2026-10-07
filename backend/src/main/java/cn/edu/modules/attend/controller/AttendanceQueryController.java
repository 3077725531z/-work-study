package cn.edu.modules.attend.controller;

import cn.edu.common.Result;
import cn.edu.modules.attend.mapper.AttendanceMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 考勤查询：A07 监控 / E06 确认列表
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceQueryController {

    private final AttendanceMapper attendanceMapper;

    @GetMapping("/list")
    public Result<Page<?>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) Long studentId) {

        var qw = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<cn.edu.modules.attend.entity.Attendance>()
                .eq(studentId != null, cn.edu.modules.attend.entity.Attendance::getStudentId, studentId)
                .orderByDesc(cn.edu.modules.attend.entity.Attendance::getId);

        return Result.ok(attendanceMapper.selectPage(new Page<>(current, size), qw));
    }
}
