package com.megumi.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

/**
 * Mybatis 配置
 * todo  MapperScan 无效问题
 * @author chenj
 */

@MapperScan("com.megumi.dao.*")
@EnableTransactionManagement

@SuppressWarnings("all")
public class MybatisConfig  implements EnvironmentAware,TransactionManagementConfigurer {

  private Environment env;


  /**
   *  配置数据库连接池
   * @return Druid 类型数据库连接池
   */
  @Bean("dataSource")
  public DataSource dataSource() {
    DruidDataSource dataSource = new DruidDataSource();
    dataSource.setDriverClassName(env.getProperty("spring.jdbc.datasource.driver"));
    dataSource.setUrl(env.getProperty("spring.jdbc.datasource.url"));
    dataSource.setUsername(env.getProperty("spring.jdbc.datasource.username"));
    dataSource.setPassword(env.getProperty("spring.jdbc.datasource.password"));

    dataSource.setInitialSize(Integer.parseInt(
            (env.getProperty("spring.datasource.druid.initialSize","5"))));
    dataSource.setMaxActive(Integer.parseInt(
            (env.getProperty("spring.datasource.druid.maxActive","10"))));
    dataSource.setMaxWait(Integer.parseInt(
            (env.getProperty("spring.datasource.druid.maxWait","3000"))));


    return  dataSource;
  }


  @Bean(name = "sqlSessionFactory")
  @Autowired
  SqlSessionFactoryBean sqlSessionFactory(MybatisProperties mybatisProperties ,DataSource dataSource) {
    SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
    factoryBean.setDataSource(dataSource);

    String mapperLocations = mybatisProperties.getMapperLocations();

    ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
    try {
      Resource[] resources = patternResolver.getResources(mapperLocations);
      factoryBean.setMapperLocations(resources);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return factoryBean;
  }

  @Bean
  MapperScannerConfigurer mapperScannerConfigurer() {
    MapperScannerConfigurer scanner = new MapperScannerConfigurer();
    scanner.setSqlSessionFactoryBeanName("sqlSessionFactory");
    scanner.setBasePackage("com.megumi.dao");
    scanner.setAnnotationClass(Mapper.class);
    return  scanner;
  }


  @Bean("annotationDrivenTransactionManager")
  @Override
  public TransactionManager annotationDrivenTransactionManager() {
    DataSourceTransactionManager manager = new DataSourceTransactionManager();
    manager.setDataSource(dataSource());
    return  manager;
  }

  @Override
  public void setEnvironment(Environment environment) {
      this.env =environment;
  }
}
