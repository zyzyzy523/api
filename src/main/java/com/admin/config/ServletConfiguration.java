package com.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;


/**
 * <p>
 * servlet注册器，注册servlet实现多线程共享
 * </p>
 *
 * @author bin.xie
 * @date 2019/5/20
 */
@Configuration
public class ServletConfiguration {

    public ServletConfiguration(DispatcherServlet dispatcherServlet) {
        dispatcherServlet.setThreadContextInheritable(true);

    }

//    @Bean(name = "fabricServletRegistrationBean")
//    public ServletRegistrationBean<DispatcherServlet> dispatcherServletRegistration(
//            DispatcherServlet dispatcherServlet) {
//        dispatcherServlet.setThreadContextInheritable(true);
//        ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean<>(dispatcherServlet);
//        registration.setLoadOnStartup(1);
//        if (this.multipartConfig != null) {
//            registration.setMultipartConfig(this.multipartConfig);
//        }
//        return registration;
//    }
}
