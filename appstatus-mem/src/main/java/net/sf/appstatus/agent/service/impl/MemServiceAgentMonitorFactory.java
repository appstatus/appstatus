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

import net.sf.appstatus.agent.service.IServiceAgentMonitor;
import net.sf.appstatus.agent.service.IServiceAgentMonitorFactory;

/**
 * In memory (cache) monitor agent factory.
 * @author Guillaume Mary
 *
 */
public class MemServiceAgentMonitorFactory implements IServiceAgentMonitorFactory {
	
	/**
	 * Monitors map
	 */
	private static Map<String, MemServiceAgentMonitor> monitors = new ConcurrentHashMap<String, MemServiceAgentMonitor>();
	
	/**
	 * {@inheritDoc}
	 */
	public IServiceAgentMonitor getMonitor(String serviceName) {
		MemServiceAgentMonitor monitor = null;
		if (monitors.containsKey(serviceName)) {
			monitor = monitors.get(serviceName);
		} else {
			monitor = new MemServiceAgentMonitor(serviceName);
			monitors.put(serviceName, monitor);
		}
		return monitor;
	}

}
