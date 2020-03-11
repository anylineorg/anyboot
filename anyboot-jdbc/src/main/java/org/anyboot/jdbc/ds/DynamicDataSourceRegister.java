package org.anyboot.jdbc.ds;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.anyline.jdbc.ds.DataSourceHolder;
import org.anyline.jdbc.ds.DynamicDataSource;
import org.anyline.util.BasicUtil;
import org.anyline.util.CharUtil;
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
        Map<String, Object> dsMap = parseConnectionParam(env,null);
        defaultDataSource = buildDataSource(dsMap);
    }
    private void initSpringDataSources(Environment env) {
        // 读取配置文件获取更多数据源
        String prefixs = env.getProperty("spring.datasource.list");
        if(null != prefixs){
	        for (String prefix : prefixs.split(",")) {
	            // 多个数据源
	            Map<String, Object> dsMap = parseConnectionParam(env, prefix);
	            DataSource ds = buildDataSource(dsMap);
	            springDataSources.put(prefix, ds);
	        	log.warn("[创建数据源][prefix:{}]",prefix);
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
    private String getProperty(Environment env, String key){
        String value = null;
        if(null == env || null == key){
            return value;
        }
        value = env.getProperty(key);
        if(null != value){
            return value;
        }
        String[] ks = key.split("-");
        String sKey = null;
        for(String k:ks){
            if(null == sKey){
                sKey = k;
            }else{
                sKey = sKey + CharUtil.toUpperCaseHeader(k);
            }
        }
        value = env.getProperty(sKey);
        return value;
    }
    private Map<String,Object> parseConnectionParam(Environment env, String prefix){
        Map<String,Object> map = new HashMap<String, Object>();
        if(BasicUtil.isNotEmpty(prefix)){
            prefix = "spring.datasource." + prefix;
        }else{
            prefix = "spring.datasource";
        }
        String type = env.getProperty(prefix + ".type");
        map.put("type", type);
        String driver = env.getProperty(prefix + ".driver");
        if(null == driver){
            driver = getProperty(env,prefix + "driver-class-name");
        }
        if(null == driver){
            driver = getProperty(env,prefix + "driver-class");
        }
        map.put("driver", driver);
        String url = env.getProperty(prefix + ".url");
        if(null == url){
            url = getProperty(env, prefix + ".jdbc-url");
        }

        String user = env.getProperty(prefix + ".user");
        if(null == url){
            url = getProperty(env, prefix + ".username");
        }

        map.put("url", url);
        map.put("username", user);
        map.put("password", env.getProperty(prefix + ".password"));
        map.put("max-idle", env.getProperty(prefix + "max-idle"));
        map.put("max-wait", env.getProperty(prefix + "max-wait"));
        map.put("min-idle", env.getProperty(prefix + "min-idle"));
        map.put("initial-size", env.getProperty(prefix + "initial-size"));
        map.put("validation-query", env.getProperty(prefix + "validation-query"));
        map.put("test-on-borrow", env.getProperty(prefix + "test-on-borrow"));
        map.put("test-while-idle", env.getProperty(prefix + "test-while-idle"));
        map.put("time-between-eviction-runs-millis", env.getProperty(prefix + "time-between-eviction-runs-millis"));

        return map;
    }

}
