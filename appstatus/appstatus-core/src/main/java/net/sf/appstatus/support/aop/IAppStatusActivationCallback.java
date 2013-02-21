package net.sf.appstatus.support.aop;

import org.aopalliance.intercept.MethodInvocation;

public interface IAppStatusActivationCallback {

	/**
	 * This method allows enable/disable AppStatus dynamically.
	 * 
	 * @param invocation
	 * @return true if AppStatus should be use for this call.
	 */
	boolean isActive(MethodInvocation invocation);

}
