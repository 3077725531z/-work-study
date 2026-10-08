package cn.edu.modules.aid.controller;

import cn.edu.common.LoginUser;
import cn.edu.common.Result;
import cn.edu.modules.aid.entity.AidApplication;
import cn.edu.modules.aid.mapper.AidMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 困难认定：申请 / 定档
 * 对应 P11 A06
 */
@RestController
@RequestMapping("/api/aid")
@RequiredArgsConstructor
public class AidController {

    private final AidMapper aidMapper;

    @PostMapping
    public Result<AidApplication> apply(@RequestBody AidApplication a) {
        // 学生强制用登录 uid
        if (LoginUser.isStudent()) {
            a.setStudentId(LoginUser.getUid());
        }
        a.setStatus("待审核");
        aidMapper.insert(a);
        return Result.ok(a);
    }

    @PostMapping("/{id}:review")
    public Result<AidApplication> review(
            @PathVariable Long id,
            @RequestParam String level) {

        AidApplication a = aidMapper.selectById(id);
        a.setLevel(level);
        a.setStatus("已认定");
        aidMapper.updateById(a);

        return Result.ok(a);
    }
}
