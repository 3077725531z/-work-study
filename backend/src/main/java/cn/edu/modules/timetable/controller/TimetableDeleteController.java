package cn.edu.modules.timetable.controller;

import cn.edu.common.BizException;
import cn.edu.common.LoginUser;
import cn.edu.common.Result;
import cn.edu.modules.timetable.entity.Timetable;
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
        Timetable t = timetableMapper.selectById(id);
        if (t == null) throw new BizException(404, "课表记录不存在");
        // 学生只能删自己的课表
        if (LoginUser.isStudent() && !t.getStudentId().equals(LoginUser.getUid())) {
            throw new BizException(403, "无权删除他人课表");
        }
        if ("work".equals(t.getSource())) {
            return Result.badRequest("在岗排班不可删，离岗后自动移除");
        }
        timetableMapper.deleteById(id);
        return Result.ok();
    }
}
