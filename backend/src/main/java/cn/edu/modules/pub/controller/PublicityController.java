package cn.edu.modules.pub.controller;

import cn.edu.common.Result;
import cn.edu.modules.pub.entity.Publicity;
import cn.edu.modules.pub.mapper.PublicityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 公示：发布 / 列表 / 已读回执
 * 对应 P13 P14 A10
 */
@RestController
@RequestMapping("/api/publicities")
@RequiredArgsConstructor
public class PublicityController {

    private final PublicityMapper publicityMapper;

    @GetMapping
    public Result<List<Publicity>> list() {
        return Result.ok(publicityMapper.selectList(null));
    }

    @PostMapping
    public Result<Publicity> publish(@RequestBody Publicity p) {
        p.setStatus("公示中");
        publicityMapper.insert(p);
        return Result.ok(p);
    }

    @PostMapping("/{id}:read")
    public Result<Map<String, Object>> read(
            @PathVariable Long id,
            @RequestParam Long studentId) {

        return Result.ok(Map.of("read", true));
    }
}
