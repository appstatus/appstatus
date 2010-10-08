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
package net.sf.appstatus.agent.service;

import net.sf.appstatus.agent.MonitorFactory;

/**
 * Service Monitor factory.
 * 
 * @author Guillaume Mary
 * 
 */
public final class ServiceMonitorFactory {

	/**
	 * Return a new {@link IServiceMonitor} instance.
	 * 
	 * @param serviceName
	 *            service name
	 * @return a new {@link IServiceMonitor} instance
	 */
	public static IServiceMonitor getMonitor(String serviceName) {
		return getMonitor(MonitorFactory.DEFAULT_MONITOR_NAME, serviceName);
	}

	/**
	 * Return a new {@link IServiceMonitor} instance.
	 * 
	 * @param monitorName
	 *            monitor name
	 * @param serviceName
	 *            service name
	 * @return a new {@link IServiceMonitor} instance
	 */
	public static IServiceMonitor getMonitor(String monitorName,
			String serviceName) {
		return MonitorFactory.getMonitor(IServiceMonitor.class, monitorName,
				serviceName);
	}

	/**
	 * Default constructor.
	 */
	private ServiceMonitorFactory() {
		// prevent instantiation
	}

}
