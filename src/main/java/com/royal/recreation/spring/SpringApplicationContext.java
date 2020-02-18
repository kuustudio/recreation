package com.royal.recreation.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringApplicationContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringApplicationContext.applicationContext = applicationContext;
    }

    public static <T> T getBean(String name) {
        Object object = null;
        if (applicationContext != null) {
            try {
                object = applicationContext.getBean(name);
            } catch (RuntimeException e) {

            }

        }
        return (T) object;
    }
}