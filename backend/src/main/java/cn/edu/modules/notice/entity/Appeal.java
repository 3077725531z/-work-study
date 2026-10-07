package cn.edu.modules.notice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * 申诉工单：工资/公示/考勤申诉统一走这里
 */
@Data
@TableName("appeals")
public class Appeal {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private String type;

    private Long refId;

    private Long studentId;

    private String content;

    private String status;

    private String reply;

    private LocalDate deadline;
}
