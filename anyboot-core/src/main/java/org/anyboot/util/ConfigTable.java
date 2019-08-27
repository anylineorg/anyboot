package org.anyboot.util;


import org.apache.log4j.Logger;


public class ConfigTable extends org.anyline.util.ConfigTable{
	private static final Logger log = Logger.getLogger(ConfigTable.class);
	protected static final String version = "2.1.5.810";
	protected static final String minVersion = "0007";
	static{
		init();
		debug();
	}

	public static void init(){
		init("anyboot");
	}
}