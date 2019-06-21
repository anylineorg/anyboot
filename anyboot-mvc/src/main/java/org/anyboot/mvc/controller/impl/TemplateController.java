package org.anyboot.mvc.controller.impl;

import org.anyline.plugin.springmvc.TemplateModelAndView;
import org.anyline.plugin.springmvc.TemplateView;
import org.anyline.util.BasicUtil;
import org.anyline.util.BeanUtil;
import org.anyline.util.ConfigTable;
import org.anyline.util.DESUtil;
import org.anyline.util.HttpUtil;
import org.anyline.util.WebUtil;
import org.springframework.web.servlet.ModelAndView;

public class TemplateController extends org.anyline.controller.impl.TemplateController{

	/**
	 * 创建显示视图
	 * @param adapt 是否识别并切换 移动 PC(web,wap)
	 * @param name
	 * @param template
	 * template(page,template) 与createView(page,template); 相同
		需要注意的是
		page有两种格式相对与绝对
		相对目录时,方法内部会将文件名与目录名拼接
		拼接时,先拼当前类的dir 再拼父类中的dir
		另外:template不指定时template(page)默认为default.jsp
		内容文件与模板文件 目录结构应该保持一致
	 * @return
	 */
	@Override
	protected TemplateModelAndView template(boolean adapt, String name, String template){
		TemplateModelAndView tv = new TemplateModelAndView();
		if(null != name && !name.startsWith("/")){
			//相对目录
			name = buildDir() + name;
		}
		String content_template = "";
		if(null != template){
			if(!template.endsWith(".jsp")){
				template += ".jsp";
			}
			if(!template.startsWith("/")){
				if(name.contains("/page/")){
					content_template = name.substring(0, name.indexOf("/page/")) + "/template/layout/" + template;
				}else{
					content_template = buildDir() + "template/layout/" + template;
				}
			}
		}
		String clientType = "web";
		if(WebUtil.isWap(getRequest())){
			clientType = "wap";
		}
		if(null != name){
			if(adapt){
				name = name.replace("/web/", "/"+clientType+"/");
				name = name.replace("/wap/", "/"+clientType+"/");
			}
			name = name.replace("${client_type}", clientType);
		}
		if(null != content_template){
			if(adapt){
				content_template = content_template.replace("/web/", "/"+clientType+"/");
				content_template = content_template.replace("/wap/", "/"+clientType+"/");
			}
			content_template = content_template.replace("${client_type}", clientType);
		}
		if(ConfigTable.isDebug() && adapt){
			log.warn("[create view template][content path:" + content_template + "][template path:" + name + "]");
		}
		tv.setViewName(content_template);
		tv.addObject(TemplateView.ANYLINE_TEMPLATE_CONTENT_PATH, name);
		
//		tv.setViewName(name);
//		tv.addObject(TemplateView.ANYLINE_TEMPLATE_NAME, content_template);
//		tv.addObject(TemplateModelAndView.CONTENT_URL,getRequest().getRequestURI());
//		String style_template = name.substring(0,name.lastIndexOf("/")+1).replace("/page/", "/template/style/");
//		try{
//			tv.addObject(TemplateView.ANYLINE_STYLE_TEMPLATE_DES, DESUtil.getInstance().encrypt(style_template));
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		String clazz = this.getClass().getName();
//		tv.setFromClass(clazz);
		return tv;
	}
	
}