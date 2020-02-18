package com.royal.recreation.config.bean.listener;

import com.royal.recreation.config.bean.MyUserDetails;
import com.royal.recreation.util.Util;
import org.springframework.security.core.context.SecurityContext;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class MySessionListener implements HttpSessionListener {

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        MyUserDetails details = (MyUserDetails) securityContext.getAuthentication().getPrincipal();
        if (details != null) {
            Util.removeLock(details.getId());
        }
    }
}
