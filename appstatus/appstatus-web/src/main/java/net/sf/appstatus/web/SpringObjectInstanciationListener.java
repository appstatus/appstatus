package net.sf.appstatus.web;

import javax.servlet.ServletContext;

import net.sf.appstatus.core.IObjectInstanciationListener;

import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SpringObjectInstanciationListener implements
		IObjectInstanciationListener {
	WebApplicationContext webApplicationContext = null;

	public SpringObjectInstanciationListener(ServletContext context) {
		webApplicationContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(context);
	}

	public Object getInstance(String className) {
		Object obj = null;

		try {
			obj = webApplicationContext.getBean(className);
		} catch (BeansException e) {
		}

		return obj;
	}

}
