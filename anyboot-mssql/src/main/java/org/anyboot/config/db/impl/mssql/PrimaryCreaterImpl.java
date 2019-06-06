
package org.anyboot.config.db.impl.mssql;

import java.util.List;

import org.anyboot.config.db.Procedure;
import org.anyboot.config.db.SQLCreater.DB_TYPE;
import org.anyboot.config.db.impl.ProcedureImpl;
import org.anyboot.dao.AnybootDao;
import org.anyboot.dao.PrimaryCreater;
import org.anyboot.util.BasicUtil;
import org.anyboot.util.ConfigTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("mssql.primaryCreaterImpl")
public class PrimaryCreaterImpl implements PrimaryCreater {

	@Autowired(required=false)
	@Qualifier("anybootDao")
	private AnybootDao dao;

	public DB_TYPE type(){
		return DB_TYPE.MSSQL;
	}
	public synchronized Object createPrimary(String table, String column, String other) {
		String primary = null;
		if(null == column){
			column = ConfigTable.getString("DEFAULT_PRIMARY_KEY");
		}
		if(null == column){
			column = "CD";
		}
		if(null == table || null == column){
			return null;
		}
		Procedure proc = new ProcedureImpl();

		proc.setName(ConfigTable.getString("CREATE_PRIMARY_KEY_PROCEDURE"));
		proc.addInput(table);
		proc.addInput(column);
		proc.addInput(other);
		proc.regOutput();
		try{
			boolean result = dao.executeProcedure(proc);
			if(result){
				List<Object> list = proc.getResult();
				if(null != list && list.size()>0){
					primary = list.get(0).toString();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(null == primary || "-1".equals(primary) || "null".equalsIgnoreCase(primary)){
			primary = BasicUtil.getRandomUpperString(10);
		}
		return primary;
	}

}
