package cn.edu.modules.notice.controller;

import cn.edu.common.Result;
import cn.edu.modules.notice.entity.Appeal;
import cn.edu.modules.notice.mapper.AppealMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 工单提交/查询/回复：P10 学生提交，A09 管理处理
 */
@RestController
@RequestMapping("/api/appeals")
@RequiredArgsConstructor
public class AppealSubmitController {

    private final AppealMapper appealMapper;

    @PostMapping
    public Result<Appeal> submit(@RequestBody Appeal appeal) {
        appeal.setStatus("待处理");
        appealMapper.insert(appeal);
        return Result.ok(appeal);
    }
}
