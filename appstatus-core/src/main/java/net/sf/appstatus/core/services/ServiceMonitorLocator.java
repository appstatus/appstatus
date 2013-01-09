package net.sf.appstatus.core.services;


import org.apache.commons.collections.ArrayStack;

/**
 * This locator allows to retrieve the current ServiceMonitor. It uses a
 * ThreadLocal internally to track all running calls.
 * 
 * @author Nicolas Richeton
 * 
 */
public class ServiceMonitorLocator {

	private static final ThreadLocal<ArrayStack> monitorStack = new ThreadLocal<ArrayStack>() {
		@Override
		protected ArrayStack initialValue() {
			return new ArrayStack();
		}
	};

	public static IServiceMonitor getCurrentServiceMonitor() {
		return (IServiceMonitor) monitorStack.get().peek();
	}

	public static ArrayStack getServiceMonitorStack() {
		return (ArrayStack) monitorStack.get();
	}

}
