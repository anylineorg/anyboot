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


package org.anyboot.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.anyboot.util.BeanUtil;
import org.apache.log4j.Logger;

public abstract class AnybootEntity extends DataRow implements Serializable{
	private static final long serialVersionUID = 1L;
	protected static final Logger log = Logger.getLogger(AnybootEntity.class);



	/**
	 * 实体类对应的列
	 * @param checkInsert
	 * 			是否检查可插入
	 * @param checkUpdate
	 * 			是否检查可更新
	 * @return
	 */
	public List<String> getColumns(boolean checkInsert, boolean checkUpdate){
		List<String> columns = new ArrayList<String>();
		/*读取类属性*/
		Class clazz = this.getClass();
		while(null != clazz){
			Field[] fields = clazz.getDeclaredFields();
			for(Field field:fields){
				String column = BeanUtil.getColumn(field, checkInsert, checkUpdate);
				if(null != column){
					columns.add(column);
				}	
			}
			clazz = clazz.getSuperclass();
		}
		return columns;
	}
	/**
	 * 根据属性读取对应的列名
	 * @param property
	 * @return
	 */
	public String getColumnByProperty(String property){
		String column = null;
		Field field = null;
		try{
			field = this.getClass().getDeclaredField(property);
		}catch(Exception ee){}
		if(null == field){
			try{
				field = this.getClass().getSuperclass().getDeclaredField(property);
			}catch(Exception ee){}
		}
		if(null != field){
			column = BeanUtil.getColumn(field, false, false);
		}
		return column;
	}
	/**
	 * 根据列名读取属性值
	 * @param column
	 * @return
	 */
	public Object getValueByColumn(String column){
		return BeanUtil.getValueByColumn(this, column);
	}


}
