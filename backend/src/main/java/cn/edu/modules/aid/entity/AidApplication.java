package cn.edu.modules.aid.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 困难认定：files 存 JSON 数组的 OSS 地址
 * 流程：申请 -> 辅导员初审 -> 学院复审 -> 公示建档
 */
@Data
@TableName("aid_applications")
public class AidApplication {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private String level;

    private BigDecimal income;

    private String reason;

    private String files;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
