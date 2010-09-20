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

import net.sf.appstatus.agent.batch.IJobProgressAgentFactory;
import net.sf.appstatus.agent.batch.JobProgressAgentFactory;
import net.sf.appstatus.agent.batch.spi.IJobProgressAgentFactoryBinder;

/**
 * The binding of {@link JobProgressAgentFactory} class with an actual instance of
 * {@link IJobProgressAgentFactory} is performed using information returned by this class.
 * 
 * @author Guillaume Mary
 */
public class StaticJobProgressAgentFactoryBinder implements
		IJobProgressAgentFactoryBinder {

	/**
	 * The unique instance of this class.
	 * 
	 */
	private static final StaticJobProgressAgentFactoryBinder SINGLETON = new StaticJobProgressAgentFactoryBinder();

	/**
	 * Return the singleton of this class.
	 * 
	 * @return the StaticJobProgressAgentFactoryBinder singleton
	 */
	public static final StaticJobProgressAgentFactoryBinder getSingleton() {
		return SINGLETON;
	}
	
	
	private static final String jobProgressAgentFactoryClassStr = MemJobProgressAgentFactory.class
			.getName();
	
	private final IJobProgressAgentFactory jobProgressAgentFactory;
	
	private StaticJobProgressAgentFactoryBinder() {
		jobProgressAgentFactory = new MemJobProgressAgentFactory();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IJobProgressAgentFactory getJobProgressAgentFactory() {
		return jobProgressAgentFactory;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getJobProgressAgentFactoryStr() {
		return jobProgressAgentFactoryClassStr;
	}

}
