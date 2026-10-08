package cn.edu.common;

/**
 * 当前登录用户上下文（ThreadLocal）
 * 由 JwtInterceptor 解析 token 后写入，请求结束自动清理
 */
public class LoginUser {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private final Long uid;
    private final String role;

    public LoginUser(Long uid, String role) {
        this.uid = uid;
        this.role = role;
    }

    public static void set(LoginUser u) {
        HOLDER.set(u);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static Long getUid() {
        LoginUser u = HOLDER.get();
        return u == null ? null : u.uid;
    }

    public static String getRole() {
        LoginUser u = HOLDER.get();
        return u == null ? null : u.role;
    }

    public static boolean isStudent() {
        return "student".equals(getRole());
    }

    public static void clear() {
        HOLDER.remove();
    }
}
