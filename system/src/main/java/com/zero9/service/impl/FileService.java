package com.zero9.service.impl;

import com.zero9.config.ProfileConfig;
import com.zero9.domain.AjaxResult;
import com.zero9.domain.model.LoginUser;
import com.zero9.redis.RedisCache;
import com.zero9.service.SysUserService;
import com.zero9.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private RedisCache redisCache;

    /**
     * 用户头像上传函数
     * @param file  文件对象
     * @return  上传成功或者失败信息
     */
    @Transactional
    public AjaxResult uploadAvatar(MultipartFile file) {

        LoginUser loginUser = SecurityUtils.getLoginUser();

        if (file == null || file.isEmpty()) {
            return AjaxResult.error("文件不能为空!");
        }
        //  获取文件上传的基本目录(C:\Users\Zero9\Desktop\MyProject\oss)
        String profile = ProfileConfig.getProfile();
        //  拼接文件上传的相对路径
        String uploadDir = Paths.get(profile, "upload", "avatar").toString();
        //  前端访问的相对路径，目前还没拼接文件名
        String accessDir = "/profile/upload/avatar/";
        //  老文件相对路径，在更新新的文件之后需要删除
        String oldFileUrl = loginUser.getUser().getAvatar();

        String updateFileUrl;
        try {
            updateFileUrl = saveFile(file, uploadDir, accessDir);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return AjaxResult.error("文件上传失败!");
        }

        loginUser.getUser().setAvatar(updateFileUrl);

        // 更新缓存（重点：key要对）
        redisCache.setObject(SecurityUtils.getToken(), loginUser);

        if (sysUserService.updateById(loginUser.getUser())) {
            try {
                deleteFile(oldFileUrl, profile);
            } catch (IOException e) {
                log.warn("删除旧头像失败: {}", oldFileUrl);
            }
            return AjaxResult.success("上传成功").put("avatarUrl", updateFileUrl);
        }

        return AjaxResult.error("数据库更新失败");
    }

    /**
     * 用户头像上传函数
     * @param file  文件对象
     * @return  上传成功或者失败信息
     */
    @Transactional
    public AjaxResult upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("文件不能为空!");
        }
        //  获取文件上传的基本目录(C:\Users\Zero9\Desktop\MyProject\oss)
        String profile = ProfileConfig.getProfile();
        //  拼接文件上传的相对路径
        String uploadDir = Paths.get(profile, "upload", "file").toString();
        //  前端访问的相对路径，目前还没拼接文件名
        String accessDir = "/profile/upload/file/";

        String updateFileUrl;
        try {
            updateFileUrl = saveFile(file, uploadDir, accessDir);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return AjaxResult.error("文件上传失败!");
        }
        return AjaxResult.success("上传成功").put("fileUrl", updateFileUrl);
    }

    /**
     * 保存头像
     * @param file  文件对象
     * @param uploadDir 上传目录
     * @param accessDir 文件访问路径
     * @return  文件真实路径(/profile/upload/avatar/xxx.xxx)
     * @throws IOException  文件IO异常
     */
    private String saveFile(MultipartFile file, String uploadDir, String accessDir) throws IOException {

        Path dir = Paths.get(uploadDir);
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }

        String original = file.getOriginalFilename();
        String extension = "";

        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf(".")).toLowerCase();
        }

        // 限制类型
        String[] allowTypes = {".jpg", ".jpeg", ".png", ".gif", ".txt"};
        boolean allowed = Arrays.asList(allowTypes).contains(extension);
        if (!allowed) {
            throw new RuntimeException("不支持的文件类型");
        }

        String fileName = UUID.randomUUID() + extension;
        Path path = Paths.get(uploadDir, fileName);

        // 推荐方式
        file.transferTo(path.toFile());

        return accessDir + fileName;
    }

    private void deleteFile(String oldFileUrl, String profile) throws IOException {

        if (oldFileUrl == null || oldFileUrl.isEmpty()) return;
        if (!oldFileUrl.startsWith("/profile/")) return;
        if (oldFileUrl.contains("default")) return;

        String relativePath = oldFileUrl.replace("/profile", "");
        Path path = Paths.get(profile, relativePath);

        Files.deleteIfExists(path);
    }

}
