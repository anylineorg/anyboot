/* 
 * Copyright 2006-2015 www.anyboot.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *          
 */


package org.anyboot.config.http;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.anyboot.config.db.Group;
import org.anyboot.config.db.GroupStore;
import org.anyboot.config.db.Order;
import org.anyboot.config.db.OrderStore;
import org.anyboot.config.db.SQL;
import org.anyboot.config.db.SQL.COMPARE_TYPE;
import org.anyboot.entity.PageNavi;


/**
 * 查询参数
 * @author Administrator
 *
 */
public interface ConfigStore {
	
	
	/**
	 * 解析查询配置参数
	 * @param configs	
	 * 			"COMPANY_CD:company","NM:nmEn% | NM:nmCn%","STATUS_VALUE:[status]"
	 * @return
	 */
	public Config parseConfig(String config);
	public ConfigStore setPageNavi(PageNavi navi);
	public ConfigStore copyPageNavi(PageNavi navi);
	public ConfigStore addParam(String key, String value);
	public ConfigStore setValue(HttpServletRequest request);
	public ConfigChain getConfigChain();
	public Config getConfig(String key);
	public ConfigStore removeConfig(String key);
	public ConfigStore removeConfig(Config config);
	public List<Object> getConfigValues(String key);
	public Object getConfigValue(String key);
	public Config getConfig(String key, SQL.COMPARE_TYPE compare);
	public ConfigStore removeConfig(String key, SQL.COMPARE_TYPE compare);
	public List<Object> getConfigValues(String key, SQL.COMPARE_TYPE compare);
	public Object getConfigValue(String key, SQL.COMPARE_TYPE compare);
	public ConfigStore addConditions(String key, Object value);
	public ConfigStore addCondition(String key, Object value);
	/**
	 * 
	 * @param key
	 * @param value
	 * @param overCondition 覆盖相同key的条件
	 * @param overValue		覆盖条件value
	 * @return
	 */
	public ConfigStore addCondition(String key, Object value, boolean overCondition, boolean overValue);
	public ConfigStore addCondition(COMPARE_TYPE compare, String key, Object value);
	public ConfigStore addCondition(COMPARE_TYPE compare, String key, Object value, boolean overCondition, boolean overValue);
	/**
	 * 添加排序
	 * @param order
	 * @return
	 */
	public ConfigStore order(Order order);

	public ConfigStore order(String column, String type);
	public ConfigStore order(String order);
	public OrderStore getOrders() ;
	public ConfigStore setOrders(OrderStore orders) ;
	/**
	 * 添加分组
	 * @param group
	 * @return
	 */
	public ConfigStore group(Group group);

	public ConfigStore group(String group);
	public GroupStore getGroups() ;
	public ConfigStore setGroups(GroupStore groups) ;
	public PageNavi getPageNavi();
	/**
	 * 提取部分查询条件
	 * @param keys
	 * @return
	 */
	public ConfigStore fetch(String ... keys);
	
	public String toString();
	/**
	 * 开启记录总数懒加载 
	 * @param ms 缓存有效期(毫秒)
	 * @return
	 */
	public ConfigStore setTotalLazy(long ms);
}


