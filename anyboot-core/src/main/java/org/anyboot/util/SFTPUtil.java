package org.anyboot.util;

public class SFTPUtil extends org.anyline.util.SFTPUtil{

	public SFTPUtil(String host, int port, String user, String password) throws Exception{
		super(host, port, user, password);
	}
	public SFTPUtil(String host, String user, String password) throws Exception{
		super(host, user, password);
	}
	public SFTPUtil(String host, String user, String password, int port) throws Exception{
		super(host, user, password, port);
	}
}