package com.zero9.controller.system.user;

import com.zero9.controller.BaseController;
import com.zero9.domain.AjaxResult;
import com.zero9.domain.model.RegisterBody;
import com.zero9.service.impl.RegisterService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterController extends BaseController {

    @Resource
    private RegisterService registerService;

    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody registerBody) {
        return success(registerService.registerUser(registerBody));
    }
}
