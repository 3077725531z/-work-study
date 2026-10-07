package cn.edu.modules.apply.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 申请：slots 存 JSON 数组，如 ["周一晚","周二晚"]
 * student_id + job_id 唯一约束保证每岗只可申请一次
 */
@Data
@TableName("applications")
public class Application {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private Long studentId;

    private Long jobId;

    private String slots;

    private String remark;

    private String attachUrl;

    private String status;

    private String deptComment;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
