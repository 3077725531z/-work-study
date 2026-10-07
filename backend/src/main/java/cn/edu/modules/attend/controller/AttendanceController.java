package cn.edu.modules.attend.controller;

import cn.edu.common.BizException;
import cn.edu.common.Result;
import cn.edu.modules.apply.entity.Application;
import cn.edu.modules.apply.mapper.ApplicationMapper;
import cn.edu.modules.attend.entity.Attendance;
import cn.edu.modules.attend.mapper.AttendanceMapper;
import cn.edu.modules.job.entity.Job;
import cn.edu.modules.job.mapper.JobMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 考勤：打卡 / 部门确认
 * 规则：仅已通过的申请可打卡；岗位定过经纬度则必须在半径内
 * 对应 P07 E06
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceMapper attendanceMapper;

    private final ApplicationMapper applicationMapper;

    private final JobMapper jobMapper;

    private final JdbcTemplate jdbc;

    @PostMapping("/clock")
    public Result<Attendance> clock(@RequestBody Map<String, Object> body) {
        Long studentId = Long.valueOf(body.get("studentId").toString());
        Long jobId = Long.valueOf(body.get("jobId").toString());

        Long ok = applicationMapper.selectCount(
                new LambdaQueryWrapper<Application>()
                        .eq(Application::getStudentId, studentId)
                        .eq(Application::getJobId, jobId)
                        .eq(Application::getStatus, "已通过"));
        if (ok == null || ok == 0) {
            throw new BizException(400, "该岗位申请未通过，不可打卡");
        }

        Job job = jobMapper.selectById(jobId);
        Double lat = body.get("lat") == null ? null : Double.valueOf(body.get("lat").toString());
        Double lng = body.get("lng") == null ? null : Double.valueOf(body.get("lng").toString());
        if (job != null && job.getLat() != null && job.getLng() != null) {
            if (lat == null || lng == null) {
                throw new BizException(400, "须开启定位并在岗点范围内打卡");
            }
            int radius = job.getRadiusM() != null ? job.getRadiusM() : defaultRadius();
            double dist = haversine(lat, lng, job.getLat(), job.getLng());
            if (dist > radius) {
                throw new BizException(400, "超出打卡范围（距岗点" + Math.round(dist) + "米，限" + radius + "米）");
            }
        }

        Attendance a = new Attendance();
        a.setStudentId(studentId);
        a.setJobId(jobId);
        a.setWorkDate(LocalDate.now());
        a.setClockIn(LocalDateTime.now());
        a.setLat(lat);
        a.setLng(lng);
        a.setStatus("正常");

        attendanceMapper.insert(a);

        return Result.ok(a);
    }

    @PostMapping("/confirm")
    public Result<Map<String, Object>> confirm(@RequestParam Long id) {
        Attendance a = attendanceMapper.selectById(id);
        if (a == null) {
            throw new BizException(400, "考勤不存在");
        }
        a.setConfirmed(1);
        attendanceMapper.updateById(a);

        return Result.ok(Map.of("confirmed", true));
    }

    private int defaultRadius() {
        try {
            String v = jdbc.queryForList("SELECT cval FROM sys_config WHERE ckey='attend.radius_m'", String.class)
                    .stream().findFirst().orElse("50");
            return Integer.parseInt(v);
        } catch (Exception e) {
            return 50;
        }
    }

    private static double haversine(double lat1, double lng1, double lat2, double lng2) {
        double r = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double x = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 2 * r * Math.asin(Math.sqrt(x));
    }
}
