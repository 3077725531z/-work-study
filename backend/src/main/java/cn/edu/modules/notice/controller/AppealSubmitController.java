package cn.edu.modules.notice.controller;

import cn.edu.common.LoginUser;
import cn.edu.common.Result;
import cn.edu.modules.notice.entity.Appeal;
import cn.edu.modules.notice.mapper.AppealMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 工单提交：P10 学生提交
 */
@RestController
@RequestMapping("/api/appeals")
@RequiredArgsConstructor
public class AppealSubmitController {

    private final AppealMapper appealMapper;

    @PostMapping
    public Result<Appeal> submit(@RequestBody Appeal appeal) {
        // 学生强制用登录 uid
        if (LoginUser.isStudent()) {
            appeal.setStudentId(LoginUser.getUid());
        }
        appeal.setStatus("待处理");
        appealMapper.insert(appeal);
        return Result.ok(appeal);
    }
}
