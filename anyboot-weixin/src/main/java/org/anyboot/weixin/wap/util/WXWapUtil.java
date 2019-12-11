package org.anyboot.weixin.wap.util;

import org.anyline.entity.DataRow;
import org.anyline.weixin.wap.util.WXWapConfig;

public class WXWapUtil extends org.anyline.weixin.wap.util.WXWapUtil{

	public WXWapUtil(WXWapConfig config) {
		super(config);
	}
	public WXWapUtil(String key, DataRow config){
		super(key, config);
	}
}