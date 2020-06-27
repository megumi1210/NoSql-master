package org.example.config;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * spring 的基本配置
 * @author chenj
 */
@Configuration
@Import(SpringRedisConfig.class)
@PropertySource("classpath:/spring.properties")
@ComponentScan("org.example.*")
public class RootConfig {

    @Bean
    PropertySourcesPlaceholderConfigurer  placeholderConfigurer(){
        return  new PropertySourcesPlaceholderConfigurer();
    }
}
