package cn.edu.modules.attend.controller;

import cn.edu.common.LoginUser;
import cn.edu.common.Result;
import cn.edu.modules.attend.entity.Attendance;
import cn.edu.modules.attend.mapper.AttendanceMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 考勤查询：学生看自己的 / 管理/部门看全部
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

        Long filterId = LoginUser.isStudent() ? LoginUser.getUid() : studentId;

        var qw = new LambdaQueryWrapper<Attendance>()
                .eq(filterId != null, Attendance::getStudentId, filterId)
                .orderByDesc(Attendance::getId);

        return Result.ok(attendanceMapper.selectPage(new Page<>(current, size), qw));
    }
}
