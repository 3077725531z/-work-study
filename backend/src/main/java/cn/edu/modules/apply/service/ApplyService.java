package cn.edu.modules.apply.service;

import cn.edu.common.BizException;
import cn.edu.modules.apply.dto.ApplyRequest;
import cn.edu.modules.apply.entity.Application;
import cn.edu.modules.apply.mapper.ApplicationMapper;
import cn.edu.modules.job.entity.Job;
import cn.edu.modules.job.mapper.JobMapper;
import cn.edu.modules.timetable.entity.Timetable;
import cn.edu.modules.timetable.mapper.TimetableMapper;
import cn.edu.modules.timetable.service.TimetableService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 申请服务：课表冲突拦截 + 防重复申请
 */
@Service
@RequiredArgsConstructor
public class ApplyService {

    private final ApplicationMapper applicationMapper;

    private final TimetableService timetableService;

    private final TimetableMapper timetableMapper;

    private final JobMapper jobMapper;

    @Value("${app.semester}")
    private String semester;

    public Application submit(ApplyRequest req) {
        if (req.getSlots().size() < 2) {
            throw new BizException(400, "至少选择2个可到岗时段");
        }
        if (!timetableService.hasCourses(req.getStudentId(), semester)) {
            throw new BizException(400, "请先填写本学期课表，再申请岗位");
        }

        var courses = timetableService.listOf(req.getStudentId(), semester);
        List<String> hits = timetableService.findConflicts(req.getSlots(), courses);
        if (!hits.isEmpty()) {
            throw new BizException(403, "与课程冲突：" + String.join("、", hits));
        }

        Long exists = applicationMapper.selectCount(
                new LambdaQueryWrapper<Application>()
                        .eq(Application::getStudentId, req.getStudentId())
                        .eq(Application::getJobId, req.getJobId()));
        if (exists != null && exists > 0) {
            throw new BizException(400, "已申请过该岗位，请勿重复提交");
        }

        Application app = new Application();
        app.setCode("A" + System.currentTimeMillis() + (int) (Math.random() * 900 + 100));
        app.setStudentId(req.getStudentId());
        app.setJobId(req.getJobId());
        app.setSlots(toJsonArray(req.getSlots()));
        app.setRemark(req.getRemark());
        app.setAttachUrl(req.getAttachUrl());
        app.setStatus("待审核");

        try {
            applicationMapper.insert(app);
        } catch (DuplicateKeyException e) {
            throw new BizException(400, "每岗限申请1次，请勿重复提交");
        }

        return app;
    }

    public Application audit(Long id, boolean pass, String comment) {
        Application app = applicationMapper.selectById(id);
        if (app == null) {
            throw new BizException(400, "申请不存在");
        }

        app.setStatus(pass ? "已通过" : "未通过");
        app.setDeptComment(comment);
        applicationMapper.updateById(app);

        if (pass) {
            roster(app);
        }

        return app;
    }

    /**
     * 通过即排班：在岗时段写入课表(source=work)，后续申请自动避让
     */
    private void roster(Application app) {
        Job job = jobMapper.selectById(app.getJobId());
        String title = job == null ? "岗位" + app.getJobId() : job.getTitle();

        for (String raw : app.getSlots().split("[,，]")) {
            String slot = raw.replaceAll("[\\[\\]\"]", "").trim();
            if (slot.isEmpty()) continue;
            int weekday = parseWeekday(slot);
            String period = parsePeriod(slot);
            if (weekday < 1 || period == null) continue;

            Long n = timetableMapper.selectCount(
                    new LambdaQueryWrapper<Timetable>()
                            .eq(Timetable::getStudentId, app.getStudentId())
                            .eq(Timetable::getSemester, semester)
                            .eq(Timetable::getWeekday, weekday)
                            .eq(Timetable::getSlot, period)
                            .eq(Timetable::getSource, "work"));
            if (n != null && n > 0) continue;

            Timetable t = new Timetable();
            t.setStudentId(app.getStudentId());
            t.setSemester(semester);
            t.setWeekday(weekday);
            t.setSlot(period);
            t.setCourse("[在岗]" + title);
            t.setWeeks("1-16周");
            t.setSource("work");
            timetableMapper.insert(t);
        }
    }

    private static int parseWeekday(String slot) {
        String cn = "一二三四五六日";
        for (int i = 0; i < cn.length(); i++) {
            if (slot.contains("周" + cn.charAt(i))) return i + 1;
        }
        return -1;
    }

    private static String parsePeriod(String slot) {
        for (String s : TimetableService.SLOTS) {
            if (slot.contains(s)) return s;
        }
        return null;
    }

    public List<Application> myList(Long studentId) {
        return applicationMapper.selectList(
                new LambdaQueryWrapper<Application>()
                        .eq(Application::getStudentId, studentId)
                        .orderByDesc(Application::getId));
    }

    /**
     * slots 列是 JSON 类型，必须存 ["周一晚","周五晚"] 这种合法 JSON
     * 之前存逗号拼接串会导致 Invalid JSON text 而 500
     */
    private static String toJsonArray(List<String> slots) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < slots.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(slots.get(i).replace("\\", "\\\\").replace("\"", "\\\"")).append("\"");
        }
        return sb.append("]").toString();
    }
}
