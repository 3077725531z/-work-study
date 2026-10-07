package cn.edu.modules.review.controller;

import cn.edu.common.Result;
import cn.edu.modules.review.mapper.ReviewMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 评价查询：P12 学生只读自己的评价
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewQueryController {

    private final ReviewMapper reviewMapper;

    @GetMapping("/mine")
    public Result<?> mine(@RequestParam Long studentId) {
        return Result.ok(reviewMapper.selectList(
                new LambdaQueryWrapper<cn.edu.modules.review.entity.Review>()
                        .eq(cn.edu.modules.review.entity.Review::getStudentId, studentId)
                        .orderByDesc(cn.edu.modules.review.entity.Review::getId)));
    }
}
