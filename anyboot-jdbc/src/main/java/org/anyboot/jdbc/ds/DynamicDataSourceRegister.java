package org.anyboot.jdbc.ds;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.anyline.jdbc.ds.DataSourceHolder;
import org.anyline.jdbc.ds.DynamicDataSource;
import org.anyline.util.BasicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
 

public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private Logger log = LoggerFactory.getLogger(DynamicDataSourceRegister.class);

    //指定默认数据源(springboot2.0默认数据源是hikari如何想使用其他数据源可以自己配置)
    private static final String DATASOURCE_TYPE_DEFAULT = "com.zaxxer.hikari.HikariDataSource";
    //默认数据源
    private DataSource defaultDataSource;
    //用户自定义数据源
    private Map<String, DataSource> springDataSources = new HashMap<>();

    @Override
    public void setEnvironment(Environment environment) {
        initDefaultDataSource(environment);
        initSpringDataSources(environment);
    }

    private void initDefaultDataSource(Environment env) {
        // 读取主数据源
        Map<String, Object> dsMap = new HashMap<>();
        String driver = env.getProperty("spring.datasource.driver");
        if(BasicUtil.isEmpty(driver)){
        	driver = env.getProperty("spring.datasource.driver-class-name");
        }
        if(BasicUtil.isEmpty(driver)){
        	driver = env.getProperty("spring.datasource.driverClassName");
        }
        dsMap.put("driver", driver);
        String url = env.getProperty("spring.datasource.url");
        if(BasicUtil.isEmpty(url)){
        	url = env.getProperty("spring.datasource.jdbc-url");
        }
        if(BasicUtil.isEmpty(url)){
        	url = env.getProperty("spring.datasource.jdbcUrl");
        }
        dsMap.put("url", url);
        dsMap.put("username", env.getProperty("spring.datasource.username"));
        dsMap.put("password", env.getProperty("spring.datasource.password"));
        defaultDataSource = buildDataSource(dsMap);
    }


    private void initSpringDataSources(Environment env) {
        // 读取配置文件获取更多数据源
        String prefixs = env.getProperty("spring.datasource.list");
        if(null != prefixs){
	        for (String prefix : prefixs.split(",")) {
	            // 多个数据源
	            Map<String, Object> dsMap = new HashMap<>();
	            String type = env.getProperty("spring.datasource." + prefix + ".type");
	            dsMap.put("type", type);
	            String driver = env.getProperty("spring.datasource." + prefix + ".driver");
	            if(BasicUtil.isEmpty(driver)){
	            	driver = env.getProperty("spring.datasource." + prefix + ".driver-class-name");
	            }
	            if(BasicUtil.isEmpty(driver)){
	            	driver = env.getProperty("spring.datasource." + prefix + ".driverClassName");
	            }
	            dsMap.put("driver", driver);
	            String url = env.getProperty("spring.datasource." + prefix + ".url");
	            if(BasicUtil.isEmpty(url)){
	            	url = env.getProperty("spring.datasource." + prefix + ".jdbc-url");
	            }
	            if(BasicUtil.isEmpty(url)){
	            	url = env.getProperty("spring.datasource." + prefix + ".jdbcUrl");
	            }
	            dsMap.put("url", url);
	            dsMap.put("username", env.getProperty("spring.datasource." + prefix + ".username"));
	            dsMap.put("password", env.getProperty("spring.datasource." + prefix + ".password"));
	            DataSource ds = buildDataSource(dsMap);
	            springDataSources.put(prefix, ds);
	        	log.warn("[创建数据源][key:{},type:{},driver:{},url:{}]",prefix, type, driver, url);
	        }
        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
        //添加默认数据源
        targetDataSources.put("dataSource", this.defaultDataSource);
        DataSourceHolder.reg("dataSource");
        //添加其他数据源
        targetDataSources.putAll(springDataSources);
        for (String key : springDataSources.keySet()) {
        	log.warn("[注册数据源][key:{}]",key);
        	DataSourceHolder.reg(key);
        }

        //创建DynamicDataSource
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);
        beanDefinition.setSynthetic(true);
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();
        mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);
        mpv.addPropertyValue("targetDataSources", targetDataSources);
        //注册 - BeanDefinitionRegistry
        beanDefinitionRegistry.registerBeanDefinition("dataSource", beanDefinition);

    }

    @SuppressWarnings("unchecked")
	public DataSource buildDataSource(Map<String, Object> dataSourceMap) {
        try {
            Object type = dataSourceMap.get("type");
            if (type == null) {
                type = DATASOURCE_TYPE_DEFAULT;// 默认DataSource
            }
            Class<? extends DataSource> dataSourceType = (Class<? extends DataSource>) Class.forName((String) type);
            String driverClassName = (String)dataSourceMap.get("driver");
            String url = (String)dataSourceMap.get("url");
            String username = (String)dataSourceMap.get("username");
            String password = (String)dataSourceMap.get("password");
            // 自定义DataSource配置
            DataSourceBuilder<?> factory = DataSourceBuilder.create()
            		.driverClassName(driverClassName)
            		.url(url)
            		.username(username)
            		.password(password)
            		.type(dataSourceType);
            return factory.build();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
