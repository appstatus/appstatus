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
