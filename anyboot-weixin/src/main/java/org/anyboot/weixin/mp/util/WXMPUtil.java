package org.anyboot.weixin.mp.util;

import org.anyline.entity.DataRow;
import org.anyline.weixin.mp.util.WXMPConfig;

public class WXMPUtil extends org.anyline.weixin.mp.util.WXMPUtil{

	public WXMPUtil(WXMPConfig config){
		super(config);
	}
	public WXMPUtil(String key, DataRow config) {
		super(key, config);
	}
}