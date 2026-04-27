package com.exampe.sys.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.exampe.common.RestBean;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

//    // 建议将路径配置在 application.yaml 中，这里为了演示方便使用常量
//    private static final String UPLOAD_DIR = "D:/uploads/";

    @Value("${spring.servlet.multipart.location}")
    private String uploadDir;


    @PostMapping("/file")
    public RestBean<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return RestBean.failure(400, "文件不能为空");
        }
        try {
            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path tempDir = base.resolve("temp").normalize();

            // 确保 temp 目录存在
            Files.createDirectories(tempDir);

            String originalName = file.getOriginalFilename();
            if (originalName == null || originalName.isEmpty()) {
                return RestBean.failure(400, "无效的文件名");
            }

            int idx = originalName.lastIndexOf('.');
            String baseName = idx > 0 ? originalName.substring(0, idx) : originalName;
            String suffix = idx > 0 ? originalName.substring(idx) : "";

            // 生成新文件名: 原始名_唯一ID.后缀
            String newFileName = baseName + "_" + IdWorker.getIdStr() + suffix;

            // 安全校验文件名
            newFileName = new File(newFileName).getName();

            Path destPath = tempDir.resolve(newFileName).normalize();

            // 防止越权（确保目标在 base 目录内）
            if (!destPath.startsWith(base)) {
                return RestBean.failure(400, "非法的文件路径");
            }

            // 保存文件到 D:/Desktop/opt/temp/...
            file.transferTo(destPath.toFile());

            // 数据库存入相对路径，例如: temp/线性代数_1764523560926.pdf
            String dbPath = "temp/" + newFileName;
            return RestBean.success("上传成功",dbPath);
        } catch (IOException e) {
            e.printStackTrace();
            return RestBean.failure(500, "上传失败: " + e.getMessage());
        }
    }


    // 通过文件名下载文件，fileName 参数应该是数据库中存储的相对路径，例如: temp/线性代数_1764523560926.pdf
    @GetMapping("/download")

    public void downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        try {
            if (fileName == null || fileName.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path resolved = base.resolve(fileName).normalize();

            // 防止路径遍历
            if (!resolved.startsWith(base)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            File file = resolved.toFile();
            if (!file.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {

                // 设置内容类型，如果是 PDF 则设为 application/pdf 以便浏览器预览
                String contentType = Files.probeContentType(resolved);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                response.setContentType(contentType);

                String rawFileName = resolved.getFileName().toString();
                // 解决下载文件名乱码问题
                String headerFileName = URLEncoder.encode(rawFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
                response.setHeader("Content-Disposition", "inline;filename=" + headerFileName);

                byte[] buffer = new byte[8192];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    //
    @DeleteMapping("/delete")
    public RestBean<String> deleteFile(@RequestParam("fileName") String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return RestBean.failure(400, "文件名不能为空");
        }

        try {
            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path resolved = base.resolve(fileName).normalize();

            // 防止路径遍历
            if (!resolved.startsWith(base)) {
                return RestBean.failure(400, "非法的文件路径");
            }

            File file = resolved.toFile();
            if (!file.exists()) {
                return RestBean.failure(404, "文件不存在");
            }

            if (file.delete()) {
                return RestBean.success("删除成功");
            } else {
                return RestBean.failure(500, "删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RestBean.failure(500, "删除失败: " + e.getMessage());
        }
    }
}
