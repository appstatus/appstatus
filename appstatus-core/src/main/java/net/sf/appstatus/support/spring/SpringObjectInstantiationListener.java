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
package net.sf.appstatus.support.spring;

import net.sf.appstatus.core.IObjectInstantiationListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 * Spring-aware instantiation listener for web applications.
 * 
 * @author Nicolas Richeton
 * 
 */
public class SpringObjectInstantiationListener implements
		IObjectInstantiationListener, ApplicationContextAware {
	private static Logger logger = LoggerFactory
			.getLogger(SpringObjectInstantiationListener.class);

	ApplicationContext applicationContext;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Current servlet context.
	 */
	public SpringObjectInstantiationListener() {
	}

	public Object getInstance(String className) {
		Object obj = null;

		try {
			obj = applicationContext.getBean(className);
		} catch (BeansException e) {
			logger.info("Unable to get Bean {}", e, className);
		}

		return obj;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
