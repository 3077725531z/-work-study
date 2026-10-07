package cn.edu.modules.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户：学生 / 用工部门账号 / 管理员共用一张表
 * 新增角色只需扩展 role 字典，无需新表
 */
@Data
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sno;

    private String passwordHash;

    private String role;

    private String name;

    private String college;

    private String grade;

    private String phone;

    private String bankCard;

    private Long deptId;

    private String status;

    private String ext;

    @TableLogic
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
