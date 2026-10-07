package cn.edu.modules.timetable.service;

import cn.edu.modules.timetable.entity.Timetable;
import cn.edu.modules.timetable.mapper.TimetableMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 课表闭环的唯一入口
 * 时段口径：1-2/3-4/5-6/7-8节为白天课，晚(9-10节)为晚上
 * 勤工岗位多在晚上，故申请时段形如"周一晚"，仅与"晚"课程/在岗冲突
 * 周次重叠才算冲突：1-8周的课不拦9-16周的班
 */
@Service
@RequiredArgsConstructor
public class TimetableService {

    public static final List<String> SLOTS = List.of("1-2节", "3-4节", "5-6节", "7-8节", "晚");

    private final TimetableMapper timetableMapper;

    public List<Timetable> listOf(Long studentId, String semester) {
        return timetableMapper.selectList(
                new LambdaQueryWrapper<Timetable>()
                        .eq(Timetable::getStudentId, studentId)
                        .eq(Timetable::getSemester, semester)
                        .orderByAsc(Timetable::getWeekday)
                        .orderByAsc(Timetable::getId));
    }

    public boolean hasCourses(Long studentId, String semester) {
        Long n = timetableMapper.selectCount(
                new LambdaQueryWrapper<Timetable>()
                        .eq(Timetable::getStudentId, studentId)
                        .eq(Timetable::getSemester, semester)
                        .eq(Timetable::getSource, "jw"));
        return n != null && n > 0;
    }

    /**
     * @param applySlots 如 ["周一晚","周三晚"]
     * @return 冲突的课程/在岗描述，空集合表示无冲突
     */
    public List<String> findConflicts(List<String> applySlots, List<Timetable> courses) {
        List<String> hits = new ArrayList<>();
        if (applySlots == null || courses == null) {
            return hits;
        }

        for (String slot : applySlots) {
            for (Timetable c : courses) {
                String weekday = "周" + "一二三四五六日".charAt(Math.max(0, Math.min(6, c.getWeekday() - 1)));
                if (slot.contains(weekday) && slot.contains(c.getSlot())
                        && weeksOverlap(slotWeeks(slot), c.getWeeks())) {
                    String tag = "work".equals(c.getSource()) ? "在岗" : "课程";
                    hits.add("《" + c.getCourse() + "》(" + weekday + c.getSlot() + "," + tag + ")");
                }
            }
        }

        return hits;
    }

    /**
     * 申请时段默认覆盖全学期，解析 "周一晚[1-8周]" 后缀则按指定周次
     */
    private static Set<Integer> slotWeeks(String slot) {
        int l = slot.indexOf('[');
        int r = slot.indexOf(']');
        if (l < 0 || r < 0) {
            return expand("1-16周");
        }
        return expand(slot.substring(l + 1, r));
    }

    public static Set<Integer> expand(String weeks) {
        Set<Integer> set = new HashSet<>();
        if (weeks == null || weeks.isBlank()) {
            for (int i = 1; i <= 16; i++) set.add(i);
            return set;
        }
        String w = weeks.trim();
        boolean odd = w.contains("单");
        boolean even = w.contains("双");
        for (String part : w.split("[,，、]")) {
            part = part.replaceAll("[^0-9\\-]", "");
            if (part.isEmpty()) continue;
            if (part.contains("-")) {
                String[] se = part.split("-");
                int s = Integer.parseInt(se[0]);
                int e = Integer.parseInt(se[1]);
                for (int i = s; i <= e; i++) set.add(i);
            } else {
                set.add(Integer.parseInt(part));
            }
        }
        if (set.isEmpty()) {
            for (int i = 1; i <= 16; i++) set.add(i);
        }
        if (odd) set.removeIf(i -> i % 2 == 0);
        if (even) set.removeIf(i -> i % 2 != 0);
        return set;
    }

    private static boolean weeksOverlap(Set<Integer> a, String b) {
        Set<Integer> bs = expand(b);
        for (Integer i : a) {
            if (bs.contains(i)) return true;
        }
        return false;
    }
}
