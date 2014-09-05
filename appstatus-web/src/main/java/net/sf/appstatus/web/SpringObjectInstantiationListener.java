/*
 * Copyright 2010 Capgemini
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 */
package net.sf.appstatus.web;

import javax.servlet.ServletContext;

import net.sf.appstatus.core.IObjectInstantiationListener;

import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * Spring-aware instantiation listener for web applications.
 * 
 * @author Nicolas Richeton
 * 
 */
public class SpringObjectInstantiationListener implements
		IObjectInstantiationListener {
	WebApplicationContext webApplicationContext = null;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Current servlet context.
	 */
	public SpringObjectInstantiationListener(ServletContext context) {
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
