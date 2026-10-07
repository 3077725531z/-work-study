package cn.edu.modules.review.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评价：仅部门可写，学生只读
 * tags 存 JSON 标签，如 ["准时","细心"]
 */
@Data
@TableName("reviews")
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long jobId;

    private Long deptId;

    private Integer stars;

    private String tags;

    private String comment;

    private LocalDateTime createTime;
}
