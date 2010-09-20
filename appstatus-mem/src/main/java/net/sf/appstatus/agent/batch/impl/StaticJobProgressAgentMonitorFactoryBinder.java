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
package net.sf.appstatus.agent.batch.impl;

import net.sf.appstatus.agent.batch.IJobProgressAgentMonitorFactory;
import net.sf.appstatus.agent.batch.JobProgressAgentMonitorFactory;
import net.sf.appstatus.agent.batch.spi.IJobProgressAgentMonitorFactoryBinder;

/**
 * The binding of {@link JobProgressAgentMonitorFactory} class with an actual instance of
 * {@link IJobProgressAgentMonitorFactory} is performed using information returned by this class.
 * 
 * @author Guillaume Mary
 */
public class StaticJobProgressAgentMonitorFactoryBinder implements
		IJobProgressAgentMonitorFactoryBinder {

	/**
	 * The unique instance of this class.
	 * 
	 */
	private static final StaticJobProgressAgentMonitorFactoryBinder SINGLETON = new StaticJobProgressAgentMonitorFactoryBinder();

	/**
	 * Return the singleton of this class.
	 * 
	 * @return the StaticJobProgressAgentMonitorFactoryBinder singleton
	 */
	public static final StaticJobProgressAgentMonitorFactoryBinder getSingleton() {
		return SINGLETON;
	}
	
	
	private static final String JobProgressAgentMonitorFactoryClassStr = MemJobProgressAgentMonitorFactory.class
			.getName();
	
	private final IJobProgressAgentMonitorFactory JobProgressAgentMonitorFactory;
	
	private StaticJobProgressAgentMonitorFactoryBinder() {
		JobProgressAgentMonitorFactory = new MemJobProgressAgentMonitorFactory();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IJobProgressAgentMonitorFactory getJobProgressAgentMonitorFactory() {
		return JobProgressAgentMonitorFactory;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getJobProgressAgentMonitorFactoryStr() {
		return JobProgressAgentMonitorFactoryClassStr;
	}

}
