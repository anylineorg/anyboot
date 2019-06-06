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


package org.anyboot.dao;

import java.util.Collection;
import org.anyboot.config.db.Procedure;
import org.anyboot.config.db.SQL;
import org.anyboot.config.http.ConfigStore;
import org.anyboot.entity.DataSet;

public interface AnybootDao{
	/**
	 * 查询
	 * @param sql
	 * @return
	 */
	public DataSet query(SQL sql, ConfigStore configs, String ... conditions);
	public DataSet query(SQL sql, String ... conditions);

	public int count(SQL sql, ConfigStore configs, String ... conditions);
	public int count(SQL sql, String ... conditions);
	
	public boolean exists(SQL sql, ConfigStore configs, String ... conditions);
	public boolean exists(SQL sql, String ... conditions);
	/**
	 * 更新
	 * @param	row
	 * 			需要更新的数据
	 * @param	columns
	 * 			需要更新的列
	 * @param	dst
	 * 			表
	 * @return
	 */
	public int update(String dst, Object data, String ... columns);
	public int update(Object data, String ... columns);
	
	/**
	 * 添加
	 * @param data
	 * 			需要插入的数据
	 * @param checkParimary
	 * 			是否需要检查重复主键,默认不检查
	 * @param columns
	 * 			需要插入的列
	 * @param dst
	 * 			表
	 * @return
	 */
	public int insert(String dst, Object data, boolean checkPrimary, String ... columns);
	public int insert(Object data, boolean checkPrimary, String ... columns);
	public int insert(String dst, Object data, String ... columns);
	public int insert(Object data, String ... columns);
	

	public int batchInsert(String dst, Object data, boolean checkPrimary, String ... columns);
	public int batchInsert(Object data, boolean checkPrimary, String ... columns);
	public int batchInsert(String dst, Object data, String ... columns);
	public int batchInsert(Object data, String ... columns);
	/**
	 * 保存(insert|update)
	 * @param data
	 * @param checkPrimary 是否检查主键
	 * @param columns
	 * @return
	 */
	public int save(String dst, Object data, boolean checkPrimary, String ... columns);
	public int save(Object data, boolean checkPrimary, String ... columns);
	public int save(String dst, Object data, String ... columns);
	public int save(Object data, String ... columns);


	public int execute(SQL sql, ConfigStore configs, String ... conditions);
	public int execute(SQL sql, String ... conditions);
	
	


	/**
	 * 执行存储过程
	 * @param procedure
	 * @param inputs
	 * @param outputs
	 * @return
	 */
	public boolean executeProcedure(Procedure procedure);
	/**
	 * 根据存储过程查询
	 * @param procedure
	 * @param inputs
	 * @return
	 */
	public DataSet queryProcedure(Procedure procedure);
	public int delete(String dest, Object data, String ... columns);
	public int delete(String table, String key, Collection<Object> values);
	public int delete(String table, String key, String ... values);
}
