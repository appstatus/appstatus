/*
 * Copyright 2012 Capgemini
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
package net.sf.appstatus.support.aop;

import net.sf.appstatus.core.services.IServiceMonitor;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Allows to define a custom service result analysis when using
 * {@link AppStatusServiceInterceptor}.
 * 
 * @author Nicolas Richeton
 * 
 */
public interface IPostServiceCallback {

	/**
	 * This method allows to analyze an exception and call monitor#failure() or
	 * monitor#error() if necessary.
	 * 
	 * @param monitor
	 * @param invocation
	 * @param e
	 */
	void handleException(IServiceMonitor monitor, MethodInvocation invocation, Exception e);

	/**
	 * This method allows to analyze the result of a service call and call
	 * monitor#failure() or monitor#error() if necessary.
	 * 
	 * @param monitor
	 * @param invocation
	 */
	void handleResult(IServiceMonitor monitor, MethodInvocation invocation);
}
