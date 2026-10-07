package cn.edu.modules.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 岗位：payTier 只存档位编码，金额以 sys_config 为准
 * payAmount 为发布时的快照，调薪不影响历史数据
 */
@Data
@TableName("jobs")
public class Job {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private String title;

    private Long deptId;

    private Integer headcount;

    private String payTier;

    private BigDecimal payAmount;

    private String workTime;

    private String place;

    private Double lat;

    private Double lng;

    private Integer radiusM;

    private String content;

    private String requirement;

    private String recruitSlots;

    private String status;

    private String rejectReason;

    private Integer viewCount;

    private Long createBy;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
