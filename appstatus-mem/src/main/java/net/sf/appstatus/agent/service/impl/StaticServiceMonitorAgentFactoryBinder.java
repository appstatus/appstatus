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

import net.sf.appstatus.agent.service.IServiceMonitorAgentFactory;
import net.sf.appstatus.agent.service.ServiceMonitorAgentFactory;
import net.sf.appstatus.agent.service.spi.IServiceMonitorAgentFactoryBinder;

/**
 * The binding of {@link ServiceMonitorAgentFactory} class with an actual instance of
 * {@link IServiceMonitorAgentFactory} is performed using information returned by this class.
 * 
 * @author Guillaume Mary
 */
public class StaticServiceMonitorAgentFactoryBinder implements
		IServiceMonitorAgentFactoryBinder {

	/**
	 * The unique instance of this class.
	 * 
	 */
	private static final StaticServiceMonitorAgentFactoryBinder SINGLETON = new StaticServiceMonitorAgentFactoryBinder();

	/**
	 * Return the singleton of this class.
	 * 
	 * @return the StaticServiceMonitorAgentFactoryBinder singleton
	 */
	public static final StaticServiceMonitorAgentFactoryBinder getSingleton() {
		return SINGLETON;
	}
	
	
	private static final String serviceMonitorAgentFactoryClassStr = MemServiceMonitorAgentFactory.class
			.getName();
	
	/**
	 * {@inheritDoc}
	 */
	public IServiceMonitorAgentFactory getServiceMonitorAgentFactory() {
		return serviceMonitorAgentFactory;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getServiceMonitorAgentFactoryStr() {
		return serviceMonitorAgentFactoryClassStr;
	}
	
	private final IServiceMonitorAgentFactory serviceMonitorAgentFactory;
	
	private StaticServiceMonitorAgentFactoryBinder() {
		serviceMonitorAgentFactory = new MemServiceMonitorAgentFactory();
	}

}
