package org.anyboot.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

public class DESUtil extends org.anyline.util.DESUtil{
	protected DESUtil() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		super();
	}
}