package com.megumi.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;

/**
 * web mvc 初始化器
 * @author chenj
 */
public class WebAppInitializer implements WebApplicationInitializer {


    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext ctx = new
                AnnotationConfigWebApplicationContext();
        ctx.register(WebConfig.class);
        ctx.register(RootConfig.class);
        ctx.setServletContext(servletContext);
       ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcher",
                       new DispatcherServlet(ctx));
        servlet.addMapping("/");
        servlet.setLoadOnStartup(1);

    }
}
