package cn.edu.modules.salary.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 工资：net = hours * rate - deduct
 * 状态机：待确认 -> 已复核 -> 已发放（锁死，只能冲正）
 */
@Data
@TableName("salaries")
public class Salary {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long jobId;

    private String month;

    private BigDecimal hours;

    private BigDecimal rate;

    private BigDecimal gross;

    private BigDecimal deduct;

    private BigDecimal net;

    private String bankTail;

    private String status;
}
