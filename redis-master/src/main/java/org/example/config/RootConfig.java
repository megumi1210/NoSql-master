package org.example.config;

import org.example.util.SpringContextUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * spring 的基本配置
 * @author chenj
 */
@Configuration
@Import({SpringRedisConfig.class,MybatisConfig.class})
@PropertySource("classpath:/spring.properties")
@ComponentScan("org.example.*")
public class RootConfig {

    @Bean
    PropertySourcesPlaceholderConfigurer  placeholderConfigurer(){
        return  new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    SpringContextUtil springContextUtil(){
        return  new SpringContextUtil();
    }
}
