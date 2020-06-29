package org.example.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * autowire 获取不到时的解决方案
 * @author chenj
 */
public final class SpringContextUtil implements ApplicationContextAware {

    private  static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
         this.context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
