package com.zero9.controller.system.user;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zero9.controller.BaseController;
import com.zero9.controller.system.user.vo.RequestUserVO;
import com.zero9.controller.system.user.vo.ResetUserPassWord;
import com.zero9.domain.AjaxResult;
import com.zero9.domain.SysMenu;
import com.zero9.domain.SysUser;
import com.zero9.domain.dto.RequestUserDTO;
import com.zero9.exception.BaseException;
import com.zero9.service.SysUserService;
import com.zero9.utils.BeanCopyUtils;
import com.zero9.utils.SecurityUtils;
import com.zero9.valid.AddValid;
import com.zero9.valid.EditValid;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;

/**
 * 后台用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Resource
    private SysUserService sysUserService;

    /*获取用户信息*/
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:user:list')")
    public AjaxResult getUsers(RequestUserDTO requestUserDTO) {
        return success(sysUserService.getUserList(requestUserDTO));
    }

    /**
     * 根据id获取用户信息
     * @param id 用户id
     * @return  用户信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:query')")
    public AjaxResult getUser(@PathVariable Long id) {
        return success(sysUserService.getUserById(id));
    }

    /**
     * 新增用户
     * @param requestUserVO 用户VO
     * @return  成功或者失败
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('system:user:add')")
    public AjaxResult add(@Validated(AddValid.class) @RequestBody RequestUserVO requestUserVO) {
        SysUser user = BeanCopyUtils.copy(requestUserVO, SysUser.class);
        AjaxResult result = validate(user);
        if (result != null) {
            return result;
        }
        //  加密密码
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getLoginUserName());
        user.setAvatar("/profile/upload/avatar/default.png");
        return toAjax(sysUserService.insertUser(user));
    }

    /*修改用户信息*/
    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public AjaxResult edit(@Validated(EditValid.class) @RequestBody RequestUserVO requestUserVO) {
        SysUser user = BeanCopyUtils.copy(requestUserVO, SysUser.class);
        AjaxResult result = validate(user);
        if (result != null) {
            return result;
        }
        user.setUpdateBy(SecurityUtils.getLoginUserName());
        return toAjax(sysUserService.updateUser(user));
    }

    /*修改用户状态*/
    @PostMapping("/change-status")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public AjaxResult changeStatus(@Validated(EditValid.class) @RequestBody RequestUserVO requestUserVO) {
        SysUser user = BeanCopyUtils.copy(requestUserVO, SysUser.class);
        user.setUpdateBy(SecurityUtils.getLoginUserName());
        return toAjax(sysUserService.updateUserStatus(user));
    }

    /*删除用户*/
    @DeleteMapping("/{userIds}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public AjaxResult remove(@PathVariable List<Long> userIds) {
        if (userIds != null && userIds.contains(SecurityUtils.getLoginUserId())) {
            return error("当前用户不能删除");
        }
        return toAjax(sysUserService.deleteUserByIds(userIds));
    }

    /*修改个人信息*/
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('system:user:update')")
    public AjaxResult update(@Validated(EditValid.class) @RequestBody RequestUserVO requestUserVO) {
        SysUser user = BeanCopyUtils.copy(requestUserVO, SysUser.class);
        AjaxResult result = validate(user);
        if (result != null) {
            return result;
        }
        if (sysUserService.updateById(user)) {
            return success();
        }
        return error();
    }

    @PostMapping("/reset")
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    public AjaxResult reset(@RequestBody ResetUserPassWord resetUserPassWord) {
        System.out.println(resetUserPassWord);
        // 1. 校验新密码和确认密码
        if (!(Objects.equals(resetUserPassWord.getNewPassWord(), resetUserPassWord.getConfirmPassWord()))) {
            return error("两次输入的新密码不一致");
        }
        // 2. 查询用户
        SysUser user = sysUserService.getUserById(resetUserPassWord.getId());
        if (user == null) {
            return error("用户不存在");
        }
        // 3. 校验旧密码
        if (!SecurityUtils.matches(resetUserPassWord.getOldPassWord(), user.getPassword())) {
            return error("旧密码错误");
        }
        // 4. 新密码加密
        String password = SecurityUtils.encryptPassword(
                resetUserPassWord.getNewPassWord()
        );
        // 5. 更新用户密码
        SysUser sysUser = new SysUser();
        sysUser.setId(resetUserPassWord.getId());
        sysUser.setPassword(password);
        boolean success = sysUserService.updateById(sysUser);
        if (!success) {
            return error("密码修改失败");
        }
        return success("修改密码成功");
    }

    /**
     * 校验用户信息
     * @param user
     * @return
     */
    private AjaxResult validate(SysUser user) {
        /**
         * sysUserService.checkUserNameUnique(user.getUsername())
         * 如果为true那么就是唯一
         */
        if((!sysUserService.checkUserNameUnique(user))) {
            return error("修改用户'" + user.getUsername() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && !sysUserService.checkPhoneUnique(user)) {
            return error("修改用户'" + user.getUsername() + "'失败，手机号已存在");
        }else if (StringUtils.isNotEmpty(user.getEmail())
                && !sysUserService.checkEmailUnique(user)) {
            return error("修改用户'" + user.getUsername() + "'失败，邮箱已存在");
        }
        return null;
    }
}
