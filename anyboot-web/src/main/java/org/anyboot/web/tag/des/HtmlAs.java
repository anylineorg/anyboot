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


package org.anyboot.web.tag.des;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.anyboot.util.WebUtil;
import org.anyboot.web.tag.BaseBodyTag;
import org.apache.log4j.Logger;
/**
 * 整体加密
 * @author Administrator
 *
 */
public class HtmlAs extends BaseBodyTag implements Cloneable{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(HtmlAs.class);

	public int doEndTag() throws JspException {
		try{
			String value = body;
			if(null != value && !"".equals(value.trim())){
				value = value.trim();
				JspWriter out = pageContext.getOut();
				out.print(WebUtil.encryptHtmlTagA(value));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			release();
		}
		return EVAL_PAGE;   
	}
	@Override
	public void release() {
		super.release();
		body = null;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
