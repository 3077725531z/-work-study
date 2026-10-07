package cn.edu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 静态映射：/files/** -> backend/uploads/
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:uploads}")
    private String dir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File folder = new File(dir);
        if (!folder.isAbsolute()) {
            folder = new File(System.getProperty("user.dir"), dir);
        }
        String location = "file:" + folder.getAbsolutePath() + "/";
        registry.addResourceHandler("/files/**").addResourceLocations(location);
    }
}
