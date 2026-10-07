package com.zero9.controller.system.user;

import com.zero9.controller.BaseController;
import com.zero9.domain.AjaxResult;
import com.zero9.domain.model.LoginBody;
import com.zero9.service.impl.LoginService;
import com.zero9.utils.IpRegionUtils;
import com.zero9.utils.IpUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 登录控制器
 */
@RestController
@RequestMapping("/auth")
public class LoginController extends BaseController {

    @Resource
    private LoginService loginService;

    //  登录接口 api
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody, HttpServletRequest request) {
        String ip = IpUtils.getIpAddr(request);
        String address = IpRegionUtils.getRegion(ip);
        System.out.println(ip+"-----"+address);
        return success(loginService.login(loginBody.getUsername(), loginBody.getPassword()));
    }

    //  获取用户信息
    @GetMapping("/getInfo")
    @PreAuthorize("hasAuthority('system:user:query')")
    public AjaxResult getInfo() {
        return success(loginService.getUser());
    }
}
