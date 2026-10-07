package cn.edu.modules.review.controller;

import cn.edu.common.Result;
import cn.edu.modules.review.entity.Review;
import cn.edu.modules.review.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 评价：部门写，学生只读
 * 对应 P12 E09
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewMapper reviewMapper;

    @PostMapping
    public Result<Review> save(@RequestBody Review review) {
        reviewMapper.insert(review);
        return Result.ok(review);
    }
}
