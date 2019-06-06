package org.anyboot.qq.mp.util;

import java.util.Hashtable;

import org.anyboot.util.BasicConfig;
import org.anyboot.util.BasicUtil;
import org.anyboot.util.ConfigTable;


public class QQMPConfig extends BasicConfig{
	private static Hashtable<String,BasicConfig> instances = new Hashtable<String,BasicConfig>();
	/**
	 * 服务号相关信息
	 */
	public String APP_ID = ""				; //AppID(应用ID)
	public String API_KEY = ""				; //APPKEY(应用密钥)
	public String OAUTH_REDIRECT_URL		; //登录成功回调URL
	static{
		init();
		debug();
	}
	public static void init() {
		//加载配置文件
		loadConfig();
	}

	public static QQMPConfig getInstance(){
		return getInstance("default");
	}
	public static QQMPConfig getInstance(String key){
		if(BasicUtil.isEmpty(key)){
			key = "default";
		}

		if(ConfigTable.getReload() > 0 && (System.currentTimeMillis() - QQMPConfig.lastLoadTime)/1000 > ConfigTable.getReload() ){
			//重新加载
			loadConfig();
		}
		return (QQMPConfig)instances.get(key);
	}
	/**
	 * 加载配置文件
	 * 首先加载anyboot-config.xml
	 * 然后加载anyboot开头的xml文件并覆盖先加载的配置
	 */
	private synchronized static void loadConfig() {
		loadConfig(instances, QQMPConfig.class, "anyboot-qq-mp.xml");
		QQMPConfig.lastLoadTime = System.currentTimeMillis();
	}
	private static void debug(){
	}
}
