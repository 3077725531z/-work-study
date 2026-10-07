package cn.edu.modules.timetable.controller;

import cn.edu.common.Result;
import cn.edu.modules.timetable.mapper.TimetableMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 课表删除：在岗排班不可删，课程可删
 */
@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableDeleteController {

    private final TimetableMapper timetableMapper;

    @DeleteMapping("/{id}")
    public Result<?> remove(@PathVariable Long id) {
        var t = timetableMapper.selectById(id);
        if (t != null && "work".equals(t.getSource())) {
            return Result.badRequest("在岗排班不可删，离岗后自动移除");
        }
        timetableMapper.deleteById(id);
        return Result.ok();
    }
}
