package cn.edu.common;

import lombok.Data;

/**
 * 统一返回体：200 成功 / 4xx 参数问题 / 5xx 服务异常
 * 403 专门留给课表冲突，前端据此标红
 */
@Data
public class Result<T> {

    private int code;

    private String msg;

    private T data;

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg("ok");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> fail(String msg) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }

    public static <T> Result<T> badRequest(String msg) {
        Result<T> r = new Result<>();
        r.setCode(400);
        r.setMsg(msg);
        return r;
    }

    public static <T> Result<T> conflict(T data) {
        Result<T> r = new Result<>();
        r.setCode(403);
        r.setMsg("与课表存在冲突");
        r.setData(data);
        return r;
    }
}
