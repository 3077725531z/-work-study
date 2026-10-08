package cn.edu.config;

import cn.edu.common.JwtUtil;
import cn.edu.common.LoginUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * JWT 拦截器：从 Authorization 头解析 token，写入 LoginUser 上下文
 * 公开路径（登录/注册/岗位浏览/公示/文件）直接放行
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            write401(response, "未登录或 token 缺失");
            return false;
        }
        String token = auth.substring(7);
        try {
            Long uid = jwtUtil.parseUid(token);
            // role 需要从 token 解析；JwtUtil 目前只解析 uid，这里简化：
            // 从 token claim 取 role
            String role = parseRole(token);
            LoginUser.set(new LoginUser(uid, role));
            return true;
        } catch (Exception e) {
            write401(response, "token 无效或已过期");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LoginUser.clear();
    }

    private String parseRole(String token) {
        try {
            return com.auth0.jwt.JWT.decode(token).getClaim("role").asString();
        } catch (Exception e) {
            return "student";
        }
    }

    private void write401(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("code", 401, "msg", msg)));
    }
}
