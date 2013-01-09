package net.sf.appstatus.core.services;

public abstract class AbstractServiceMonitor implements IServiceMonitor {

	private final boolean useThreadLocal;

	/**
	 * This class implements Thread local support for ServiceMonitor.
	 * 
	 * @param useThreadLocal
	 */
	public AbstractServiceMonitor(boolean useThreadLocal) {
		this.useThreadLocal = useThreadLocal;
	}

	public void beginCall(Object... parameters) {
		if (useThreadLocal) {
			ServiceMonitorLocator.getServiceMonitorStack().push(this);
		}
	}

	public void endCall() {
		if (useThreadLocal) {
			ServiceMonitorLocator.getServiceMonitorStack().remove(this);
		}
	}

}
