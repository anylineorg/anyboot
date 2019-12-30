package org.anyboot.util;

public class ConfigTable extends org.anyline.util.ConfigTable{
	static{
		init();
		debug();
	}

	public static void init(){
		init("anyboot");
	}
}