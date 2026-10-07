package cn.edu.common;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传：图片/PDF ≤5MB，返回 /files/** 地址存业务表
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStore fileStore;

    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return Result.badRequest("文件为空");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.badRequest("单个文件限5MB");
        }
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();
        if (!name.matches(".*\\.(jpg|jpeg|png|pdf)$")) {
            return Result.badRequest("仅支持 jpg/png/pdf");
        }
        return Result.ok(fileStore.save(file));
    }
}
