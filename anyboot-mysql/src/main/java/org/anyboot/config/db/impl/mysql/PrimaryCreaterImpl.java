package org.anyboot.config.db.impl.mysql;

import org.anyline.dao.AnylineDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("anyboot.mysql.primaryCreaterImpl")
public class PrimaryCreaterImpl extends org.anyline.config.db.impl.mysql.PrimaryCreaterImpl {

	@Autowired(required=false)
	@Qualifier("anybootDao")
	private AnylineDao dao;

}
