package org.anyboot.mvc.controller.impl;

import org.anyline.controller.impl.AnylineController;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AnybootController extends AnylineController{

	@Autowired(required = false)
	@Qualifier("anybootService")
	protected AnylineService service;
}