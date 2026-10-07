package cn.edu.modules.attend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤：部门确认后锁定，修改必须走申诉
 */
@Data
@TableName("attendances")
public class Attendance {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long jobId;

    private LocalDate workDate;

    private LocalDateTime clockIn;

    private LocalDateTime clockOut;

    private BigDecimal hours;

    private String status;

    private Double lat;

    private Double lng;

    private Long confirmBy;

    private Integer confirmed;
}
