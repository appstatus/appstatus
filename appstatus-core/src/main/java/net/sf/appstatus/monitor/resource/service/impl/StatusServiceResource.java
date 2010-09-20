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
import net.sf.appstatus.agent.service.IServiceAgentMonitor;
import net.sf.appstatus.agent.service.ServiceAgentMonitorFactory;
import net.sf.appstatus.monitor.resource.ResourceType;
import net.sf.appstatus.monitor.resource.service.IStatusServiceResource;

/**
 * Implementation of {@link IStatusServiceResource}
 * 
 * @author Guillaume Mary
 * 
 */
public class StatusServiceResource implements IStatusServiceResource {

	private IStatusChecker statusChecker;

	private IServiceAgentMonitor serviceAgentMonitor;

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
		this.serviceAgentMonitor = ServiceAgentMonitorFactory.getMonitor(uid);
	}

	/**
	 * {@inheritDoc}
	 */
	public float getAverageFlow(String operationName) {
		return serviceAgentMonitor.getAverageFlow(operationName);
	}

	/**
	 * {@inheritDoc}
	 */
	public float getAverageResponseTime(String operationName) {
		return serviceAgentMonitor.getAverageResponseTime(operationName);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getOperationNames() {
		return serviceAgentMonitor.getOperationNames();
	}

	/**
	 * {@inheritDoc}
	 */
	public IStatusResult getStatus() {
		return statusChecker.checkStatus();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getType() {
		return ResourceType.SERVICE.getLabel();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUid() {
		return uid;
	}

	public void setServiceAgentMonitor(IServiceAgentMonitor statisticsProvider) {
		this.serviceAgentMonitor = statisticsProvider;
	}

	public void setStatusChecker(IStatusChecker statusChecker) {
		this.statusChecker = statusChecker;
	}

}
