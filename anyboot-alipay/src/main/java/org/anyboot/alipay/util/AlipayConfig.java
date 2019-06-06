package org.anyboot.alipay.util;

import java.util.Hashtable;

import org.anyboot.entity.PageNaviConfig;
import org.anyboot.util.BasicConfig;
import org.anyboot.util.BasicUtil;
import org.anyboot.util.ConfigTable;


public class AlipayConfig extends BasicConfig{
	private static Hashtable<String,BasicConfig> instances = new Hashtable<String,BasicConfig>();
	public String APP_PRIVATE_KEY = "";
	public String ALIPAY_PUBLIC_KEY = "";
	public String APP_ID = "";
	public String DATA_FORMAT = "json";
	public String ENCODE = "utf-8";
	public String SIGN_TYPE = "RSA";
	public String RETURN_URL= "";
	public String NOTIFY_URL= "";
	static{
		init();
		debug();
	}
	public static AlipayConfig getInstance(){
		return getInstance("default");
	}
	public static AlipayConfig getInstance(String key){
		if(BasicUtil.isEmpty(key)){
			key = "default";
		}
		if(ConfigTable.getReload() > 0 && (System.currentTimeMillis() - AlipayConfig.lastLoadTime)/1000 > ConfigTable.getReload() ){
			//重新加载
			loadConfig();
		}
		
		return (AlipayConfig)instances.get(key);
	}
	public static void init() {
		//加载配置文件
		loadConfig();
	}
	/**
	 * 加载配置文件
	 * 首先加载anyboot-config.xml
	 * 然后加载anyboot开头的xml文件并覆盖先加载的配置
	 */
	private synchronized static void loadConfig() {
		loadConfig(instances, AlipayConfig.class, "anyboot-alipay.xml");
		AlipayConfig.lastLoadTime = System.currentTimeMillis();
	}
	public String getString(String key){
		return kvs.get(key);
	}
	private static void debug(){
	}
}
