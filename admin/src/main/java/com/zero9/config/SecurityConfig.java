package com.zero9.config;

import com.zero9.security.fillter.JwtAuthenticationTokenFilter;
import com.zero9.security.handler.AccessDeniedHandlerImpl;
import com.zero9.security.handler.AnonymousAuthenticationHandler;
import com.zero9.security.handler.LogoutSuccessImplHandler;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(securedEnabled = true) //启用方法级别的安全控制
public class SecurityConfig {

    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Resource
    private AnonymousAuthenticationHandler anonymousAuthenticationHandler;
    @Resource
    private AccessDeniedHandlerImpl accessDeniedHandler;
    @Resource
    private LogoutSuccessImplHandler logoutSuccessImplHandler;

    /**
     * 后台权限配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 开启跨域支持
                .cors(Customizer.withDefaults())
                // 禁用csrf防护（前后端分离基于jwt进行认证）
                .csrf(AbstractHttpConfigurer::disable)
                // 响应头配置-防止点击劫持攻击的工具
                // 不允许嵌入到其他网页
                .headers(headers -> {
                    headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
                })
                // 禁用session，无状态认证
                .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 异常处理: 认证失败处理
                .exceptionHandling(e -> e
                        //  未登录处理
                        .authenticationEntryPoint(anonymousAuthenticationHandler)
                        // 已登录无权限处理
                        .accessDeniedHandler(accessDeniedHandler)
                )
                // 权限路径配置
                .authorizeHttpRequests(auth -> auth
                        // 公开访问的接口
                        .requestMatchers("/auth/login", "/profile/**").permitAll()
                        // 其他接口需要登录后访问
                        .anyRequest().authenticated()
                )
                // 退出登录后处理
                .logout(logout -> logout
                        .logoutUrl("/logout").logoutSuccessHandler(logoutSuccessImplHandler)
                )
                // 添加JWT过滤器
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // 使用BCrypt对密码进行加密
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}