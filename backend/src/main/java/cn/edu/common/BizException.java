package cn.edu.common;

import lombok.Getter;

/**
 * 业务异常：service 层只抛这个，controller 不写 try-catch
 */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        super(message);
        this.code = 500;
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
}
