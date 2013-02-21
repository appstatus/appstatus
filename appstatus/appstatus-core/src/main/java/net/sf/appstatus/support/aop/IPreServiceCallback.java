package net.sf.appstatus.support.aop;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.services.IServiceMonitor;

import org.aopalliance.intercept.MethodInvocation;

public interface IPreServiceCallback {
	/**
	 * This method allows to create the AppStatus monitor according to the
	 * invocation. Can be used to set custom group and name.
	 * 
	 * @param invocation
	 * @return The IServiceMonitor to use or null if defaults should be used.
	 */
	IServiceMonitor getMonitor(AppStatus appStatus, MethodInvocation invocation);

	/**
	 * This method allows to setup the AppStatus monitor according to the
	 * invocation. Can be used to add context informations or change logger.
	 * 
	 * @param monitor
	 * @param invocation
	 */
	void setup(IServiceMonitor monitor, MethodInvocation invocation);
}
