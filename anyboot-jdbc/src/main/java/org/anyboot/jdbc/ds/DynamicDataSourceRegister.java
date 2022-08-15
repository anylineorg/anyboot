package org.anyboot.jdbc.ds;

import org.anyline.jdbc.ds.DataSourceHolder;
import org.anyline.jdbc.ds.DynamicDataSource;
import org.anyline.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private Logger log = LoggerFactory.getLogger(DynamicDataSourceRegister.class);

    //指定默认数据源(springboot2.0默认数据源是hikari如何想使用其他数据源可以自己配置)
    private static final String DATASOURCE_TYPE_DEFAULT = "com.zaxxer.hikari.HikariDataSource";
    //默认数据源
    private DataSource defaultDataSource;
    //用户自定义数据源
    private static Map<String, DataSource> springDataSources = new HashMap<>();


    @Override
    public void setEnvironment(Environment environment) {
        initDefaultDataSource(environment);
        initSpringDataSources(environment);
    }
    private void initDefaultDataSource(Environment env) {
        // 读取主数据源
        defaultDataSource = buildDataSource("spring.datasource",env);
    }
    private void initSpringDataSources(Environment env) {
        // 读取配置文件获取更多数据源
        String prefixs = env.getProperty("spring.datasource.list");
        if(null != prefixs){
            for (String prefix : prefixs.split(",")) {
                // 多个数据源
                DataSource ds = buildDataSource("spring.datasource."+prefix,env);
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
    public static DataSource buildDataSource(String prefix, Environment env) {
        try {
            if(BasicUtil.isNotEmpty(prefix) && !prefix.endsWith(".")){
                prefix += ".";
            }
            String type = getProperty(prefix,env, "type");
            if (type == null) {
                type = DATASOURCE_TYPE_DEFAULT;// 默认DataSource
            }

            Class<? extends DataSource> dataSourceType = (Class<? extends DataSource>) Class.forName(type);
            String driverClassName = getProperty(prefix, env, "driver","driver-class","driver-class-name");
            String url = getProperty(prefix, env, "url","jdbc-url");
            String username = getProperty(prefix, env,"user","username");
            String password = getProperty(prefix, env, "password");
            // 自定义DataSource配置
            DataSourceBuilder<?> factory = DataSourceBuilder.create()
                    .driverClassName(driverClassName)
                    .url(url)
                    .username(username)
                    .password(password)
                    .type(dataSourceType);
            DataSource ds = factory.build();
            //先取默认配置
            setFieldsValue(ds, "spring.datasource.", env);
            setFieldsValue(ds, prefix, env);
            SpringContextUtil.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(ds);
            return ds;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据配置文件设置对象属性值
     * @param obj 对象
     * @param prefix 前缀
     * @param env 配置文件环境
     */
    private static void setFieldsValue(Object obj, String prefix, Environment env ){
        List<String> fields = ClassUtil.getFieldsName(obj.getClass());
        for(String field:fields){
            String value = getProperty(prefix, env, field);
            if(BasicUtil.isNotEmpty(value)) {
                BeanUtil.setFieldValue(obj, field, value);
            }
        }
    }

    /**
     * 根据配置文件提取指定key的值
     * @param prefix 前缀
     * @param env 配置文件环境
     * @param keys key列表 第一个有值的key生效
     * @return String
     */
    private static String getProperty(String prefix, Environment env, String ... keys){
        String value = null;
        if(null == env || null == keys){
            return value;
        }
        if(null == prefix){
            prefix = "";
        }
        for(String key:keys){
            key = prefix + key;
            value = env.getProperty(key);
            if(null != value){
                return value;
            }
            //以中划线分隔的配置文件
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
            if(null != value){
                return value;
            }

            //以下划线分隔的配置文件
            ks = key.split("_");
            sKey = null;
            for(String k:ks){
                if(null == sKey){
                    sKey = k;
                }else{
                    sKey = sKey + CharUtil.toUpperCaseHeader(k);
                }
            }
            value = env.getProperty(sKey);
            if(null != value){
                return value;
            }
        }
        return value;
    }

}
