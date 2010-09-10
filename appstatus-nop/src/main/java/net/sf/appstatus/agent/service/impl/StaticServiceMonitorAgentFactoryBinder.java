package net.sf.appstatus.agent.service.impl;

import net.sf.appstatus.agent.service.IServiceMonitorAgentFactory;
import net.sf.appstatus.agent.service.helpers.NOPServiceMonitorAgentFactory;
import net.sf.appstatus.agent.service.spi.IServiceMonitorAgentFactoryBinder;

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
	
	
	private static final String serviceMonitorAgentFactoryClassStr = NOPServiceMonitorAgentFactory.class
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
		serviceMonitorAgentFactory = new NOPServiceMonitorAgentFactory();
	}

}
