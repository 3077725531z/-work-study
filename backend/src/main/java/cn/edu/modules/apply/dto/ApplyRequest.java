package cn.edu.modules.apply.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 提交申请入参
 * slots 至少选 2 段，后端再与课表做冲突校验
 */
@Data
public class ApplyRequest {

    @NotNull(message = "学生不能为空")
    private Long studentId;

    @NotNull(message = "岗位不能为空")
    private Long jobId;

    @NotEmpty(message = "至少选择2个可到岗时段")
    private List<String> slots;

    private String remark;

    private String attachUrl;
}
