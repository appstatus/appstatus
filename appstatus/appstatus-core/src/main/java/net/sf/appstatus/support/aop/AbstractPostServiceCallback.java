package net.sf.appstatus.support.aop;

import net.sf.appstatus.core.services.IServiceMonitor;

import org.aopalliance.intercept.MethodInvocation;

public abstract class AbstractPostServiceCallback implements IPostServiceCallback {

	public void handleException(IServiceMonitor monitor, MethodInvocation invocation, Exception e) {
		// Nothing
	}

	public void handleResult(IServiceMonitor monitor, MethodInvocation invocation) {
		// Nothing
	}

}
