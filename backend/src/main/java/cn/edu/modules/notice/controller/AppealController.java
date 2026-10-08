package cn.edu.modules.notice.controller;

import cn.edu.common.LoginUser;
import cn.edu.common.Result;
import cn.edu.modules.notice.entity.Appeal;
import cn.edu.modules.notice.mapper.AppealMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 工单查询与回复：A09 申诉
 */
@RestController
@RequestMapping("/api/appeals")
@RequiredArgsConstructor
public class AppealController {

    private final AppealMapper appealMapper;

    @GetMapping("/list")
    public Result<Page<?>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) Long studentId) {

        Long filterId = LoginUser.isStudent() ? LoginUser.getUid() : studentId;

        var qw = new LambdaQueryWrapper<Appeal>()
                .eq(filterId != null, Appeal::getStudentId, filterId)
                .orderByDesc(Appeal::getId);

        return Result.ok(appealMapper.selectPage(new Page<>(current, size), qw));
    }

    @PostMapping("/{id}:reply")
    public Result<?> reply(@PathVariable Long id, @RequestParam String reply) {
        var a = appealMapper.selectById(id);
        a.setReply(reply);
        a.setStatus("已处理");
        appealMapper.updateById(a);
        return Result.ok();
    }
}
