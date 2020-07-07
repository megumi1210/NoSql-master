package com.megumi.config;


import com.megumi.config.MybatisConfig;
import com.megumi.config.RedisConfig;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * spring 的基本配置
 * @author chenj
 */
@Configuration
@Import({RedisConfig.class, MybatisConfig.class})
@PropertySource("classpath:/spring.properties")
@ComponentScan("com.megumi.*")
public class RootConfig {

    @Bean
    PropertySourcesPlaceholderConfigurer  placeholderConfigurer(){
        return  new PropertySourcesPlaceholderConfigurer();
    }


}
