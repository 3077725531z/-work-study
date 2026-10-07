package cn.edu.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录入参：学号 / 工号 + 密码
 */
@Data
public class LoginRequest {

    @NotBlank(message = "学号不能为空")
    private String sno;

    @NotBlank(message = "密码不能为空")
    private String password;
}
