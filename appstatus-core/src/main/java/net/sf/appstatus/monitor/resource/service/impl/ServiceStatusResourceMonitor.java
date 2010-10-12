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
package net.sf.appstatus.monitor.resource.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.appstatus.IPropertyProvider;
import net.sf.appstatus.IStatusChecker;
import net.sf.appstatus.agent.service.IServiceStatisticsMonitor;
import net.sf.appstatus.agent.service.ServiceMonitorFactory;
import net.sf.appstatus.monitor.resource.impl.BasicStatusResourceMonitor;

/**
 * Implementation of {@link IStatusServiceResourceMonitor}
 * 
 * @author Guillaume Mary
 * 
 */
public class ServiceStatusResourceMonitor extends BasicStatusResourceMonitor {

	private IServiceStatisticsMonitor serviceStatisticsMonitor;

	/**
	 * Default constructor.
	 * 
	 * @param id
	 *            resource id
	 * @param name
	 *            resource name
	 * @param type
	 *            resource type
	 * @param statusCheckers
	 *            status checkers
	 * @param propertyProviders
	 *            property providers
	 */
	public ServiceStatusResourceMonitor(String id, String name, String type,
			Set<IStatusChecker> statusCheckers,
			Set<IPropertyProvider> propertyProviders, String monitorName) {
		super(id, name, type, statusCheckers, propertyProviders);
		if (monitorName != null) {
			this.serviceStatisticsMonitor = ServiceMonitorFactory.getMonitor(monitorName,
					id);
		}
		this.serviceStatisticsMonitor = ServiceMonitorFactory.getMonitor(id);
	}

	@Override
	public Map<String, Map<String, String>> getDetails() {
		Map<String, Map<String, String>> details = new HashMap<String, Map<String, String>>();
		// get operations
		List<String> operations = serviceStatisticsMonitor.getOperationNames();

		// add average flow
		Map<String, String> detail = new HashMap<String, String>();
		for (String operation : operations) {
			detail.put(operation,
					String.valueOf(serviceStatisticsMonitor.getAverageFlow(operation)));
		}
		details.put("Average Flow", detail);

		// add average response time
		detail = new HashMap<String, String>();
		for (String operation : operations) {
			detail.put(operation, String.valueOf(serviceStatisticsMonitor
					.getAverageResponseTime(operation)));
		}
		details.put("Average Response Time", detail);

		return details;
	}

	public void setServiceAgentMonitor(IServiceStatisticsMonitor statisticsProvider) {
		this.serviceStatisticsMonitor = statisticsProvider;
	}
}
