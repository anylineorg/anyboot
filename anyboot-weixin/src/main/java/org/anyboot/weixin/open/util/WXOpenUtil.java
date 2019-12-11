package org.anyboot.weixin.open.util;

import org.anyline.entity.DataRow;

public class WXOpenUtil extends org.anyline.weixin.open.util.WXOpenUtil{

	public WXOpenUtil(WXOpenConfig config) {
		super(config);
	}
	public WXOpenUtil(String key, DataRow config) {
		super(key, config);
	}
}