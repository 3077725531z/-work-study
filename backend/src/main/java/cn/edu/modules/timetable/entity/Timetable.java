package cn.edu.modules.timetable.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 课表：课表闭环的核心表
 * weekday 1=周一..7=周日，slot 如 1-2节 / 晚
 */
@Data
@TableName("timetables")
public class Timetable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private String semester;

    private Integer weekday;

    private String slot;

    private String course;

    private String weeks;

    private String source;
}
