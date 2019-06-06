package org.anyboot.alipay.util;

import java.util.Hashtable;

import org.anyboot.alipay.entity.AlipayTradeOrder;
import org.anyboot.alipay.entity.AlipayTradeQuery;
import org.anyboot.alipay.entity.AlipayTradeQueryResult;
import org.anyboot.alipay.entity.AlipayTransfer;
import org.anyboot.alipay.entity.AlipayTransferQuery;
import org.anyboot.alipay.entity.AlipayTransferQueryResult;
import org.anyboot.alipay.entity.AlipayTransferResult;
import org.anyboot.util.BasicUtil;
import org.anyboot.util.BeanUtil;
import org.apache.log4j.Logger;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayFundTransOrderQueryRequest;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayFundTransOrderQueryResponse;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;

public class AlipayUtil extends org.anyline.alipay.util.AlipayUtil{
	
}
