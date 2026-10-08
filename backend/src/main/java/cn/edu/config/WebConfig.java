package cn.edu.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web 配置：静态资源映射 + JWT 拦截器
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:uploads}")
    private String dir;

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File folder = new File(dir);
        if (!folder.isAbsolute()) {
            folder = new File(System.getProperty("user.dir"), dir);
        }
        String location = "file:" + folder.getAbsolutePath() + "/";
        registry.addResourceHandler("/files/**").addResourceLocations(location);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/**",          // 登录/注册
                        "/api/jobs/**",          // 岗位浏览（公开）
                        "/api/publicities",      // 公示列表（公开）
                        "/api/files/upload",     // 文件上传（前端直接调，暂时放行）
                        "/api/sys-config/**",    // 系统配置/字典（公开）
                        "/error"
                );
    }
}
