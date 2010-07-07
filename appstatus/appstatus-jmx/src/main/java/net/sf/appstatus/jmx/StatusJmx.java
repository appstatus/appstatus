/*
 * Copyright 2010 Capgemini Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sf.appstatus.jmx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.appstatus.IStatusResult;
import net.sf.appstatus.StatusService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * JMX exposure of StatusChecker beans.
 * 
 * @author LABEMONT
 * 
 */
@ManagedResource(objectName = "AppStatus:bean=ServicesStatusChecker")
public class StatusJmx implements ApplicationContextAware {

	private static StatusService statusService = null;

	private ApplicationContext applicationContext;
	private final Logger logger = LoggerFactory.getLogger(StatusJmx.class);

	private boolean useSpring = true;

	@ManagedAttribute(description = "Status list", currencyTimeLimit = 15)
	public Map<String, String> getStatus() {
		Map<String, String> statusChecker = new HashMap<String, String>();
		for (IStatusResult result : statusService.checkAll()) {
			statusChecker.put(result.getProbeName(),
					formatCodeDisplay(result.getCode()));
		}
		return statusChecker;
	}

	@ManagedAttribute(description = "Full status list : display return code, description and resolution steps.", currencyTimeLimit = 15)
	public Map<String, List<String>> getFullStatus() {
		Map<String, List<String>> statusChecker = new HashMap<String, List<String>>();
		List<String> statusAttributs = null;
		for (IStatusResult result : statusService.checkAll()) {
			statusAttributs = new ArrayList<String>();
			statusAttributs.add(formatCodeDisplay(result.getCode()));
			statusAttributs.add(result.getDescription());
			statusAttributs.add(result.getResolutionSteps());
			statusChecker.put(result.getProbeName(), statusAttributs);
		}
		return statusChecker;
	}

	/**
	 * Human readable code format.
	 * 
	 * @param code
	 * @return the code from {@link IStatusResult} or the int code if not found
	 */
	protected String formatCodeDisplay(int code) {
		String codeDisplay = "";
		switch (code) {
		case (IStatusResult.ERROR):
			codeDisplay = "ERROR";
			break;
		case (IStatusResult.OK):
			codeDisplay = "OK";
			break;
		default:
			codeDisplay = Integer.toString(code);
		}
		return codeDisplay;
	}

	/**
	 * Load configuration from /status-jmx-conf.properties file.<br/>
	 * If not found look for Spring beans.
	 */
	public void init() {
		statusService = new StatusService();

		statusService
				.setObjectInstanciationListener(new SpringBeanInstantiationListener(
						this.applicationContext));

		statusService.init();
	}

	public void setApplicationContext(
			ApplicationContext springApplicationContext) throws BeansException {
		this.applicationContext = springApplicationContext;
	}
}
