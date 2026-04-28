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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传控制器
 * <p>提供文件上传、下载、删除等功能，支持路径安全防护</p>
 *
 * @author admin
 */
@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Value("${spring.servlet.multipart.location}")
    private String uploadDir;


    /**
     * 上传文件
     * <p>生成唯一文件名并保存到临时目录，返回相对路径</p>
     *
     * @param file 上传的文件
     * @return 文件相对路径
     */
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

            // 保存文件
            file.transferTo(destPath.toFile());

            // 数据库存入相对路径，例如: temp/线性代数_1764523560926.pdf
            String dbPath = "temp/" + newFileName;
            return RestBean.success("上传成功", dbPath);
        } catch (IOException e) {
            e.printStackTrace();
            return RestBean.failure(500, "上传失败: " + e.getMessage());
        }
    }


    /**
     * 下载文件
     * <p>通过文件名下载文件，支持浏览器预览PDF等格式</p>
     *
     * @param fileName 文件相对路径（数据库中存储的路径）
     * @param response HTTP响应对象
     */
    @GetMapping("/download")
    public void downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        try {
            if (fileName == null || fileName.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path resolved = base.resolve(fileName).normalize();

            // 防止路径遍历攻击
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

                // 设置内容类型
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

    /**
     * 删除文件
     *
     * @param fileName 文件相对路径
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public RestBean<String> deleteFile(@RequestParam("fileName") String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return RestBean.failure(400, "文件名不能为空");
        }

        try {
            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path resolved = base.resolve(fileName).normalize();

            // 防止路径遍历攻击
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

    /**
     * 批量删除文件
     * <p>支持一次性删除多个文件，返回每个文件的删除结果</p>
     *
     * @param fileNames 文件相对路径列表
     * @return 批量删除结果，包含成功和失败的文件信息
     */
    @DeleteMapping("/batch-delete")
    public RestBean<Object> batchDeleteFiles(@RequestBody List<String> fileNames) {
        if (fileNames == null || fileNames.isEmpty()) {
            return RestBean.failure(400, "文件列表不能为空");
        }

        List<String> successFiles = new ArrayList<>();
        List<Map<String, String>> failedFiles = new ArrayList<>();

        try {
            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();

            for (String fileName : fileNames) {
                if (fileName == null || fileName.isEmpty()) {
                    Map<String, String> failedInfo = new HashMap<>();
                    failedInfo.put("fileName", "空文件名");
                    failedInfo.put("reason", "文件名不能为空");
                    failedFiles.add(failedInfo);
                    continue;
                }

                try {
                    Path resolved = base.resolve(fileName).normalize();

                    // 防止路径遍历攻击
                    if (!resolved.startsWith(base)) {
                        Map<String, String> failedInfo = new HashMap<>();
                        failedInfo.put("fileName", fileName);
                        failedInfo.put("reason", "非法的文件路径");
                        failedFiles.add(failedInfo);
                        continue;
                    }

                    File file = resolved.toFile();
                    if (!file.exists()) {
                        Map<String, String> failedInfo = new HashMap<>();
                        failedInfo.put("fileName", fileName);
                        failedInfo.put("reason", "文件不存在");
                        failedFiles.add(failedInfo);
                        continue;
                    }

                    if (file.delete()) {
                        successFiles.add(fileName);
                    } else {
                        Map<String, String> failedInfo = new HashMap<>();
                        failedInfo.put("fileName", fileName);
                        failedInfo.put("reason", "删除失败");
                        failedFiles.add(failedInfo);
                    }
                } catch (Exception e) {
                    Map<String, String> failedInfo = new HashMap<>();
                    failedInfo.put("fileName", fileName);
                    failedInfo.put("reason", "删除异常: " + e.getMessage());
                    failedFiles.add(failedInfo);
                }
            }

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successFiles.size());
            result.put("failedCount", failedFiles.size());
            result.put("successFiles", successFiles);
            result.put("failedFiles", failedFiles);

            if (failedFiles.isEmpty()) {
                return RestBean.success("全部删除成功", result);
            } else if (successFiles.isEmpty()) {
                return RestBean.failure(500, "全部删除失败", result);
            } else {
                return RestBean.success("部分删除成功", result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RestBean.failure(500, "批量删除失败: " + e.getMessage());
        }
    }
}
