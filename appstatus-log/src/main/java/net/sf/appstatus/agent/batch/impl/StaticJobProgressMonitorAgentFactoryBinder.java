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

import net.sf.appstatus.agent.batch.IJobProgressMonitorAgentFactory;
import net.sf.appstatus.agent.batch.JobProgressMonitorAgentFactory;
import net.sf.appstatus.agent.batch.spi.IJobProgressMonitorAgentFactoryBinder;

/**
 * The binding of {@link JobProgressMonitorAgentFactory} class with an actual instance of
 * {@link IJobProgressMonitorAgentFactory} is performed using information returned by this class.
 * 
 * @author Guillaume Mary
 */
public class StaticJobProgressMonitorAgentFactoryBinder implements
		IJobProgressMonitorAgentFactoryBinder {

	/**
	 * The unique instance of this class.
	 * 
	 */
	private static final StaticJobProgressMonitorAgentFactoryBinder SINGLETON = new StaticJobProgressMonitorAgentFactoryBinder();

	/**
	 * Return the singleton of this class.
	 * 
	 * @return the StaticServiceMonitorAgentFactoryBinder singleton
	 */
	public static final StaticJobProgressMonitorAgentFactoryBinder getSingleton() {
		return SINGLETON;
	}
	
	
	private static final String jobProgressMonitorAgentFactoryClassStr = LogJobProgressMonitorAgentFactory.class
			.getName();
	
	private final IJobProgressMonitorAgentFactory jobProgressMonitorAgentFactory;
	
	private StaticJobProgressMonitorAgentFactoryBinder() {
		jobProgressMonitorAgentFactory = new LogJobProgressMonitorAgentFactory();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IJobProgressMonitorAgentFactory getJobProgressMonitorAgentFactory() {
		return jobProgressMonitorAgentFactory;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getJobProgressMonitorAgentFactoryStr() {
		return jobProgressMonitorAgentFactoryClassStr;
	}

}
