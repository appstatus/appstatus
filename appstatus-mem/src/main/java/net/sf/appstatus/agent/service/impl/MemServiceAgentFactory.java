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

import net.sf.appstatus.agent.service.IServiceAgent;
import net.sf.appstatus.agent.service.IServiceAgentFactory;
import net.sf.appstatus.agent.service.ServiceAgentMonitorFactory;

/**
 * In memory (cache) monitor agent factory.
 * @author Guillaume Mary
 *
 */
public class MemServiceAgentFactory implements
		IServiceAgentFactory {
	
	/**
	 * Agents map
	 */
	private static Map<String, MemServiceAgent> agents = new ConcurrentHashMap<String, MemServiceAgent>();
	
	/**
	 * {@inheritDoc}
	 */
	public IServiceAgent getAgent(String serviceName) {
		MemServiceAgent agent = null;
		if (agents.containsKey(serviceName)) {
			agent = agents.get(serviceName);
		} else {
			agent = new MemServiceAgent(serviceName);
			agent.addObserver(ServiceAgentMonitorFactory.getMonitor(serviceName));
			agents.put(serviceName, agent);
		}
		return agent;
	}
}
