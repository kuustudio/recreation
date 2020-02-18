package com.royal.recreation.config.bean.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Desc: 登录验证码认证filter
 */
public class VerifyCodeAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public VerifyCodeAuthenticationFilter() {
        AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher("/user/logined", "POST");
        this.setRequiresAuthenticationRequestMatcher(requestMatcher);
        this.setAuthenticationManager(getAuthenticationManager());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String verifyCode = request.getParameter("vcode");
        String dbVerifyCode = (String) request.getSession().getAttribute("vcode");
        if (StringUtils.isEmpty(verifyCode) || dbVerifyCode == null || !dbVerifyCode.toUpperCase().equals(verifyCode.toUpperCase())) {
            throw new VerifyCodeErrorException("verifyCode error");
        }
        return super.attemptAuthentication(request, response);
    }

    public static class VerifyCodeErrorException extends AuthenticationException {
        public VerifyCodeErrorException(String message) {
            super(message);
        }
    }


}
