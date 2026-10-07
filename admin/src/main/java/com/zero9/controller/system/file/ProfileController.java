package com.zero9.controller.system.file;

import com.zero9.controller.BaseController;
import com.zero9.domain.AjaxResult;
import com.zero9.service.impl.FileService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件操作接口
 */
@RestController
@RequestMapping("/upload")
public class ProfileController extends BaseController {

    @Resource
    private FileService fileService;

    /**
     * 文件上传
     * @param file 文件
     * @return  返回上传成功后的文件相对路径
     */
    @PostMapping("/file")
    public AjaxResult upload(@RequestParam MultipartFile file) {
        return fileService.upload(file);
    }

    /**dsadasd
     * 用户头像上传
     * @param file 用户头像文件
     * @return  返回上传成功后的图片相对路径
     */
    @PostMapping("/avatar")
    public AjaxResult uploadAvatar(@RequestParam MultipartFile file) {
        return fileService.uploadAvatar(file);
    }
}
