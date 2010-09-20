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

import net.sf.appstatus.agent.service.IServiceAgentMonitorFactory;
import net.sf.appstatus.agent.service.ServiceAgentMonitorFactory;
import net.sf.appstatus.agent.service.spi.IServiceAgentMonitorFactoryBinder;

/**
 * The binding of {@link ServiceAgentMonitorFactory} class with an actual instance of
 * {@link IServiceAgentMonitorFactory} is performed using information returned by this class.
 * 
 * @author Guillaume Mary
 */
public class StaticServiceAgentMonitorFactoryBinder implements
		IServiceAgentMonitorFactoryBinder {

	/**
	 * The unique instance of this class.
	 * 
	 */
	private static final StaticServiceAgentMonitorFactoryBinder SINGLETON = new StaticServiceAgentMonitorFactoryBinder();

	/**
	 * Return the singleton of this class.
	 * 
	 * @return the StaticServiceMonitorAgentFactoryBinder singleton
	 */
	public static final StaticServiceAgentMonitorFactoryBinder getSingleton() {
		return SINGLETON;
	}
	
	
	private static final String serviceAgentMonitorFactoryClassStr = MemServiceAgentMonitorFactory.class
			.getName();
	
	/**
	 * {@inheritDoc}
	 */
	public IServiceAgentMonitorFactory getServiceAgentMonitorFactory() {
		return serviceAgentMonitorFactory;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getServiceAgentMonitorFactoryStr() {
		return serviceAgentMonitorFactoryClassStr;
	}
	
	private final IServiceAgentMonitorFactory serviceAgentMonitorFactory;
	
	private StaticServiceAgentMonitorFactoryBinder() {
		serviceAgentMonitorFactory = new MemServiceAgentMonitorFactory();
	}

}
