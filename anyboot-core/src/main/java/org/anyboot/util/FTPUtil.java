package org.anyboot.util;


public class FTPUtil extends org.anyline.util.FTPUtil{
	public FTPUtil(String host, String user, String password) {
		super(host, user, password);
	}

	public FTPUtil(String host, String user, String password, int port) {
		super(host,user,password,port);
	}
}
