package cn.edu.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT 工具：签发与解析 token
 * 后续若接统一身份认证，只需替换这里的实现
 */
@Component
public class JwtUtil {

    @Value("${app.jwt-secret}")
    private String secret;

    @Value("${app.jwt-expire-hours:12}")
    private long expireHours;

    public String sign(Long userId, String role) {
        Date expire = new Date(System.currentTimeMillis() + expireHours * 3600_000L);

        return JWT.create()
                .withClaim("uid", userId)
                .withClaim("role", role)
                .withExpiresAt(expire)
                .sign(Algorithm.HMAC256(secret));
    }

    public Long parseUid(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token)
                .getClaim("uid")
                .asLong();
    }
}
