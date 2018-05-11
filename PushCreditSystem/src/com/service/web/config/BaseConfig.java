package com.service.web.config;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.json.JacksonFactory;
import com.jfinal.template.Engine;
import com.service.web.controller.PushCreditController;


public class BaseConfig extends JFinalConfig {
	

	public void configConstant(Constants me) {
		me.setDevMode(true);
		me.setJsonFactory(new JacksonFactory());
	}
	
	public void configRoute(Routes me) {
		me.add("oapi/store",PushCreditController.class);
	}
	
	public void configEngine(Engine me) {

	}

	public void configPlugin(Plugins me) {
	}
	

	public void configInterceptor(Interceptors me) {
	}
	
	public void configHandler(Handlers me) {
	}
}
