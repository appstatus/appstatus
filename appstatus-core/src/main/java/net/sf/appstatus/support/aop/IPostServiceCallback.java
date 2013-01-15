package net.sf.appstatus.support.aop;

import net.sf.appstatus.core.services.IServiceMonitor;

import org.aopalliance.intercept.MethodInvocation;

public interface IPostServiceCallback {

	void handleException(IServiceMonitor monitor, MethodInvocation invocation, Exception e);

	void handleResult(IServiceMonitor monitor, MethodInvocation invocation);
}
