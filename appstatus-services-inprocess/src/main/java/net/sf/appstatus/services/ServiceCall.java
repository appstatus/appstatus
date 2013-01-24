package net.sf.appstatus.services;

import net.sf.appstatus.core.services.AbstractLoggingServiceMonitor;

public class ServiceCall extends AbstractLoggingServiceMonitor {

	String id;
	Service service;

	public void cacheHit() {
		if (!this.cacheHit) {
			service.cacheHits.incrementAndGet();
		}

		// register cache hit
		super.cacheHit();
	}

	public ServiceCall(Service service, boolean log, boolean useThreadLocal) {
		super(service, log, useThreadLocal);
		this.service = service;
	}

	public String getId() {
		return id;
	}

	public void beginCall(Object... parameters) {
		// Register parameters
		super.beginCall(parameters);

		service.hits.incrementAndGet();
		service.running.incrementAndGet();
	}

	public void endCall() {
		if (endTime != null) {
			// endCall was called twice ! returning directly.
			return;
		}

		// Register end time
		super.endCall();

		service.running.decrementAndGet();

		service.addCall(executionTime, cacheHit, failure, error);
	}
}
