package com.zero9.exception;

import com.zero9.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 用户不存在
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult handleUserNotFound(UsernameNotFoundException e) {
        log.error("用户不存在: {}", e.getMessage());
        return AjaxResult.error(500, e.getMessage());
    }

    /**
     * 用户名或密码错误
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult handleBadPassword(BadCredentialsException e) {
        log.error("登录认证失败: {}", e.getMessage());
        return AjaxResult.error(500, e.getMessage());
    }

    /**
     * Spring Security 认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public AjaxResult handleAuth(AuthenticationException e) {
        log.error("认证异常: {}", e.getMessage());
        return AjaxResult.error(401, e.getMessage());
    }

    /**
     * SQL语法/数据库异常
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult handleSqlException(BadSqlGrammarException e) {
        log.error("SQL执行异常", e);
        return AjaxResult.error(500, "系统内部错误，请联系管理员");
    }

    /**
     * 资源不存在
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AjaxResult handleNoResource(NoResourceFoundException e) {
        log.error("资源不存在: {}", e.getMessage());
        return AjaxResult.error(404, "访问的资源不存在!");
    }

    /**
     * @Valid 参数校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AjaxResult handleValidException(MethodArgumentNotValidException e) {

        FieldError fieldError = e.getBindingResult().getFieldError();

        String msg = (fieldError != null)
                ? fieldError.getDefaultMessage()
                : "参数错误";

        return AjaxResult.error(400, msg);
    }

    /**
     * 自定义业务异常
     */
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult handleOther(BaseException e) {
        log.error("业务异常", e);
        return AjaxResult.error(500, "系统异常");
    }

    /**
     * 兜底异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult handleException(Exception e) {
        log.error("系统异常", e);
        return AjaxResult.error(500, "系统内部错误");
    }
}