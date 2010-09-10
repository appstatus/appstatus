package net.sf.appstatus.agent.batch.impl;

import net.sf.appstatus.agent.batch.IJobProgressMonitorAgentFactory;
import net.sf.appstatus.agent.batch.helpers.NOPJobProgressMonitorAgentFactory;
import net.sf.appstatus.agent.batch.spi.IJobProgressMonitorAgentFactoryBinder;

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
	 * @return the StaticJobProgressMonitorAgentFactoryBinder singleton
	 */
	public static final StaticJobProgressMonitorAgentFactoryBinder getSingleton() {
		return SINGLETON;
	}

	private static final String jobProgressMonitorAgentFactoryClassStr = NOPJobProgressMonitorAgentFactory.class
			.getName();

	private final IJobProgressMonitorAgentFactory jobProgressMonitorAgentFactory;

	private StaticJobProgressMonitorAgentFactoryBinder() {
		jobProgressMonitorAgentFactory = new NOPJobProgressMonitorAgentFactory();
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
