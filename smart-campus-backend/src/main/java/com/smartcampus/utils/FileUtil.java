package com.smartcampus.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件处理工具类
 */
public class FileUtil {

    // 文件存储基础路径
    private static final String BASE_PATH = "upload/"; // 实际项目中应配置为外部路径

    /**
     * 上传文件
     * @param file 文件
     * @param type 文件类型（可选，用于分类存储）
     * @return 文件相对路径
     */
    public static String uploadFile(MultipartFile file, String type) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + suffix;

        Path uploadPath;
        if (type != null && !type.isEmpty()) {
            uploadPath = Paths.get(BASE_PATH, type);
        } else {
            uploadPath = Paths.get(BASE_PATH, "other");
        }

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path targetPath = uploadPath.resolve(newFileName);
        Files.copy(file.getInputStream(), targetPath);

        // 返回相对路径，并统一使用正斜杠，避免 Windows/Linux 路径差异导致前端访问问题
        return targetPath.toString().replace("\\", "/");
    }

    /**
     * 下载文件
     * @param filePath 文件相对路径
     * @param response HttpServletResponse
     */
    public static void downloadFile(String filePath, HttpServletResponse response) throws IOException {
        Path targetPath = Paths.get(filePath);
        if (!Files.exists(targetPath)) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        String fileName = targetPath.getFileName().toString();
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
        response.setContentType(Files.probeContentType(targetPath));

        try (InputStream is = new BufferedInputStream(new FileInputStream(targetPath.toFile()));
             OutputStream os = new BufferedOutputStream(response.getOutputStream())) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        }
    }

    /**
     * 删除文件
     * @param filePath 文件相对路径
     */
    public static boolean deleteFile(String filePath) {
        Path targetPath = Paths.get(filePath);
        try {
            return Files.deleteIfExists(targetPath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
