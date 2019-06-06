package org.anyboot.weixin.mp.entity;

import org.anyboot.weixin.entity.TransferResult;

public class WXMPTransferResult extends TransferResult{
	public WXMPTransferResult(){
		super();
	}
	public WXMPTransferResult(boolean result){
		super(result, null);
	}
	public WXMPTransferResult(boolean result, String msg){
		super(result, msg);
	}
}
