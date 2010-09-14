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
package net.sf.appstatus.agent.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.appstatus.agent.service.IServiceMonitorAgent;
import net.sf.appstatus.agent.service.IServiceMonitorAgentFactory;
import net.sf.appstatus.monitor.resource.service.statistics.IServiceMonitorStatisticsProvider;
import net.sf.appstatus.monitor.resource.service.statistics.IServiceMonitorStatisticsProviderFactory;

/**
 * In memory (cache) monitor agent factory.
 * @author Guillaume Mary
 *
 */
public class MemServiceMonitorAgentFactory implements
		IServiceMonitorAgentFactory,IServiceMonitorStatisticsProviderFactory {
	
	/**
	 * Agents map
	 */
	private static Map<String, Object> agents = new ConcurrentHashMap<String, Object>();
	
	/**
	 * {@inheritDoc}
	 */
	public IServiceMonitorAgent getAgent(String serviceName) {
		IServiceMonitorAgent agent = null;
		if (agents.containsKey(serviceName)) {
			agent = (IServiceMonitorAgent) agents.get(serviceName);
		} else {
			agent = new MemServiceMonitorAgent(serviceName);
			agents.put(serviceName, agent);
		}
		return agent;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IServiceMonitorStatisticsProvider getProvider(String serviceName) {
		IServiceMonitorStatisticsProvider agent = null;
		if (agents.containsKey(serviceName)) {
			agent = (IServiceMonitorStatisticsProvider) agents.get(serviceName);
		} else {
			agent = new MemServiceMonitorAgent(serviceName);
			agents.put(serviceName, agent);
		}
		return agent;
	}

}
