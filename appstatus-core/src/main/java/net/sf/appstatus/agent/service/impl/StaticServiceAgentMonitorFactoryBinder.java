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
import net.sf.appstatus.agent.service.spi.IServiceAgentMonitorFactoryBinder;

/**
 * Dummy binder use to compile. Should be excluded.
 * 
 * @author Guillaume Mary
 * 
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
	 * @return the StaticServiceAgentMonitorFactoryBinder singleton
	 */
	public static final StaticServiceAgentMonitorFactoryBinder getSingleton() {
		return SINGLETON;
	}

	private StaticServiceAgentMonitorFactoryBinder() {
		throw new UnsupportedOperationException(
				"This code should have never made it into the jar");
	}

	/**
	 * {@inheritDoc}
	 */
	public IServiceAgentMonitorFactory getServiceAgentMonitorFactory() {
		throw new UnsupportedOperationException(
				"This code should never make it into the jar");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getServiceAgentMonitorFactoryStr() {
		throw new UnsupportedOperationException(
				"This code should never make it into the jar");
	}

}
