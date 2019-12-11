package org.anyboot.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigTable extends org.anyline.util.ConfigTable{
	private static final Logger log = LoggerFactory.getLogger(ConfigTable.class);
	protected static final String version = "2.2.0.819";
	protected static final String minVersion = "0007";
	static{
		init();
		debug();
	}

	public static void init(){
		init("anyboot");
	}
}