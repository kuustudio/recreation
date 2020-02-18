package com.royal.recreation.config;

import com.alibaba.fastjson.JSON;
import com.royal.recreation.config.bean.filter.VerifyCodeAuthenticationFilter;
import com.royal.recreation.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Objects;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Value("${system.security.salt}")
    private String salt;

    @Autowired
    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        byte[] saltBytes = salt.getBytes();
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return MD5Util.string2MD5(rawPassword.toString(), saltBytes);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return Objects.equals(encode(rawPassword), encodedPassword);
            }
        };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().headers().frameOptions().sameOrigin();
        http.requestCache().requestCache(new NullRequestCache());

        VerifyCodeAuthenticationFilter verifyCodeAuthenticationFilter = new VerifyCodeAuthenticationFilter();
        verifyCodeAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        verifyCodeAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        verifyCodeAuthenticationFilter.setAuthenticationManager(authenticationManager());
        http.addFilterBefore(verifyCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers().disable();
        http.authorizeRequests()
                .antMatchers("/css/**", "/images/**", "/js/**", "/layui/**", "/newskin/**", "/skin/**", "/user/login", "/user/vcode/**", "/user/logined").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/user/login")
                .loginProcessingUrl("/user/logined")
                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler())
                .and()
                .logout()
                .logoutUrl("/user/logout");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/favicon.ico");
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            request.getSession().removeAttribute("vcode");
            responseJson(response, true);
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            if (exception.getClass() == LockedException.class) {
                response.setHeader("X-Error-Message-Times", "1");
                response.setHeader("X-Error-Message", URLEncoder.encode("账号已被冻结,请联系管理员", "UTF-8"));
            } else if (exception.getClass() == VerifyCodeAuthenticationFilter.VerifyCodeErrorException.class) {
                response.setHeader("X-Error-Message-Times", "1");
                response.setHeader("X-Error-Message", URLEncoder.encode("验证码不正确", "UTF-8"));
            } else {
                response.setHeader("X-Error-Message-Times", "1");
                response.setHeader("X-Error-Message", URLEncoder.encode("账号密码错误", "UTF-8"));
            }
        };
    }

    private void responseJson(HttpServletResponse response, Object body) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(body));
    }

}
