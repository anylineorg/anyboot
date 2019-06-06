package org.anyboot.config.db.sql.auto.impl;

import org.anyline.config.db.SQL.COMPARE_TYPE;
import org.anyline.config.http.Config;

public class AutoConditionImpl extends org.anyline.config.db.sql.auto.impl.AutoConditionImpl{
	private static final long serialVersionUID = 1L;
	public AutoConditionImpl(Config config){
		super(config);
	}
	public AutoConditionImpl(boolean required, boolean strictRequired, String column, Object values, COMPARE_TYPE compare){
		super(required, strictRequired, column, values, compare);
	}
	public AutoConditionImpl(String text){
		super(text);
	}
}