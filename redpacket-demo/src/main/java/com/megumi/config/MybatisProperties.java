package com.megumi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * myBaits 配置类可以拓展其他配置
 * @author chenj
 */
@Component
public class MybatisProperties {

    @Value("${spring.mybatis.mapper-locations}")
    private String mapperLocations=null;

    public String getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
    }
}
