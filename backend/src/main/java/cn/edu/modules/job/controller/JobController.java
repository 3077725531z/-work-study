package cn.edu.modules.job.controller;

import cn.edu.common.Result;
import cn.edu.modules.job.entity.Job;
import cn.edu.modules.job.mapper.JobMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbc;

    @GetMapping
    public Result<Page<Job>> page(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            String key,
            Long deptId,
            String status,
            @RequestParam(required = false) String sort) {

        LambdaQueryWrapper<Job> qw = new LambdaQueryWrapper<Job>()
                .like(key != null, Job::getTitle, key)
                .eq(deptId != null, Job::getDeptId, deptId)
                .eq(status != null, Job::getStatus, status);

        // 排序：recommend 按浏览量(热门) / new 按发布时间 / pay 按薪资
        if ("recommend".equals(sort)) {
            qw.orderByDesc(Job::getViewCount);
        } else if ("pay".equals(sort)) {
            qw.orderByDesc(Job::getPayAmount);
        } else {
            qw.orderByDesc(Job::getId); // new 默认最新
        }

        return Result.ok(jobMapper.selectPage(new Page<>(current, size), qw));
    }

    @GetMapping("/{id}")
    public Result<Job> one(@PathVariable Long id) {
        Job job = jobMapper.selectById(id);
        job.setViewCount((job.getViewCount() == null ? 0 : job.getViewCount()) + 1);
        jobMapper.updateById(job);
        // 在岗人数：已通过申请的学生数
        Long cnt = jdbc.queryForObject(
                "SELECT COUNT(*) FROM applications WHERE job_id=? AND status='已通过'",
                Long.class, id);
        job.setOnboardCount(cnt == null ? 0 : cnt.intValue());
        return Result.ok(job);
    }

    @PostMapping
    public Result<Job> publish(@RequestBody Job job) {
        job.setStatus("待审核");
        // 管理员发布时未指定部门，默认归学工处（id=1）
        if (job.getDeptId() == null) job.setDeptId(1L);
        if (job.getPayAmount() == null) job.setPayAmount(java.math.BigDecimal.ZERO);
        if (job.getHeadcount() == null) job.setHeadcount(1);
        if (job.getViewCount() == null) job.setViewCount(0);
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

    /** 下架岗位 */
    @PutMapping("/{id}/offline")
    public Result<Job> offline(@PathVariable Long id) {
        Job job = jobMapper.selectById(id);
        if (job == null) return Result.fail("岗位不存在");
        job.setStatus("已下架");
        jobMapper.updateById(job);
        return Result.ok(job);
    }
}
