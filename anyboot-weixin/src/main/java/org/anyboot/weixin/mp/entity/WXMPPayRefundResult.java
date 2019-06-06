package org.anyboot.weixin.mp.entity;

import org.anyboot.weixin.entity.RefundResult;



public class WXMPPayRefundResult  extends RefundResult{
	public WXMPPayRefundResult(){
		super();
	}
	public WXMPPayRefundResult(boolean result){
		super(result, null);
	}
	public WXMPPayRefundResult(boolean result, String msg){
		super(result, msg);
	}
}