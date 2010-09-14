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

import java.util.List;

import net.sf.appstatus.IStatusChecker;
import net.sf.appstatus.IStatusResult;
import net.sf.appstatus.monitor.resource.ResourceType;
import net.sf.appstatus.monitor.resource.service.IStatusServiceResource;
import net.sf.appstatus.monitor.resource.service.statistics.IServiceMonitorStatisticsProvider;
import net.sf.appstatus.monitor.resource.service.statistics.ServiceMonitorStatisticsProviderFactory;

/**
 * Implementation of {@link IStatusServiceResource}
 * 
 * @author Guillaume Mary
 * 
 */
public class StatusServiceResource implements IStatusServiceResource {

	private IStatusChecker statusChecker;

	private IServiceMonitorStatisticsProvider statisticsProvider;

	private final String name;

	private final String uid;

	/**
	 * Default constructor.
	 * 
	 * @param uid
	 *            unique id
	 * @param name
	 *            service name
	 */
	public StatusServiceResource(String uid, String name) {
		this.uid = uid;
		this.name = name;
		this.statisticsProvider = ServiceMonitorStatisticsProviderFactory
				.getProvider(uid);
	}

	public float getAverageFlow(String operationName) {
		return statisticsProvider.getAverageFlow(operationName);
	}

	public float getAverageResponseTime(String operationName) {
		return statisticsProvider.getAverageResponseTime(operationName);
	}

	public String getName() {
		return this.name;
	}

	public List<String> getOperationNames() {
		return statisticsProvider.getOperationNames();
	}

	public IStatusResult getStatus() {
		return statusChecker.checkStatus();
	}

	public String getType() {
		return ResourceType.SERVICE.getLabel();
	}

	public String getUid() {
		return uid;
	}

	public void setStatisticsProvider(
			IServiceMonitorStatisticsProvider statisticsProvider) {
		this.statisticsProvider = statisticsProvider;
	}

	public void setStatusChecker(IStatusChecker statusChecker) {
		this.statusChecker = statusChecker;
	}

}
