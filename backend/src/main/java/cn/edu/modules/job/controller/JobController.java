package cn.edu.modules.job.controller;

import cn.edu.common.Result;
import cn.edu.modules.job.entity.Job;
import cn.edu.modules.job.mapper.JobMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 岗位：发布 / 大厅 / 审核
 * 对应 P03 P04 E03 E04 A03
 */
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobMapper jobMapper;

    @GetMapping
    public Result<Page<Job>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            String key,
            Long deptId,
            String status) {

        LambdaQueryWrapper<Job> qw = new LambdaQueryWrapper<Job>()
                .like(key != null, Job::getTitle, key)
                .eq(deptId != null, Job::getDeptId, deptId)
                .eq(status != null, Job::getStatus, status)
                .orderByDesc(Job::getId);

        return Result.ok(jobMapper.selectPage(new Page<>(current, size), qw));
    }

    @GetMapping("/{id}")
    public Result<Job> one(@PathVariable Long id) {
        Job job = jobMapper.selectById(id);
        job.setViewCount((job.getViewCount() == null ? 0 : job.getViewCount()) + 1);
        jobMapper.updateById(job);
        return Result.ok(job);
    }

    @PostMapping
    public Result<Job> publish(@RequestBody Job job) {
        job.setStatus("待审核");
        jobMapper.insert(job);
        return Result.ok(job);
    }

    @PostMapping("/{id}:publish")
    public Result<Job> approve(@PathVariable Long id) {
        Job job = jobMapper.selectById(id);
        job.setStatus("招募中");
        jobMapper.updateById(job);
        return Result.ok(job);
    }

    @PostMapping("/{id}:reject")
    public Result<Job> reject(
            @PathVariable Long id,
            @RequestParam String reason) {

        Job job = jobMapper.selectById(id);
        job.setStatus("驳回");
        job.setRejectReason(reason);
        jobMapper.updateById(job);
        return Result.ok(job);
    }
}
