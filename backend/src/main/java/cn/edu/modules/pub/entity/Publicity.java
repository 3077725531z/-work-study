package cn.edu.modules.pub.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公示：content 存脱敏后的拟录名单 JSON
 * 学生必须已读回执后才可上岗打卡
 */
@Data
@TableName("publicities")
public class Publicity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String period;

    private String content;

    private String status;

    private Long createBy;

    private LocalDateTime createTime;
}
