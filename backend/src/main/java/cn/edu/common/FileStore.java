package cn.edu.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * 本地文件存储：演示够用，正式换 OSS 只需改此类
 * 上传目录 backend/uploads，访问路径 /files/**
 */
@Component
public class FileStore {

    @Value("${app.upload-dir:uploads}")
    private String dir;

    public Map<String, String> save(MultipartFile file) throws Exception {
        String name = file.getOriginalFilename();
        String ext = name != null && name.contains(".") ? name.substring(name.lastIndexOf(".")) : "";
        String key = UUID.randomUUID().toString().replace("-", "") + ext;

        File folder = new File(dir);
        if (!folder.isAbsolute()) {
            folder = new File(System.getProperty("user.dir"), dir);
        }
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException("上传目录创建失败：" + folder.getAbsolutePath());
        }
        file.transferTo(new File(folder, key));

        return Map.of("url", "/files/" + key, "name", name == null ? key : name);
    }
}
