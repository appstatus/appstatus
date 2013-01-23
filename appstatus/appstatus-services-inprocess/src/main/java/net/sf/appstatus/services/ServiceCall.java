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

		// Update statistics
		if (cacheHit) {
			if (service.maxResponseTimeWithCache == null || service.maxResponseTimeWithCache < executionTime) {
				service.maxResponseTimeWithCache = executionTime;
			}

			if (service.minResponseTimeWithCache == null || service.minResponseTimeWithCache > executionTime) {
				service.minResponseTimeWithCache = executionTime;
			}

			service.avgResponseTimeWithCache = (service.avgResponseTimeWithCache * (service.cacheHits.get() - 1) + executionTime)
					/ service.cacheHits.get();
		} else {

			if (service.maxResponseTime == null || service.maxResponseTime < executionTime) {
				service.maxResponseTime = executionTime;
			}

			if (service.minResponseTime == null || service.minResponseTime > executionTime) {
				service.minResponseTime = executionTime;
			}

			service.avgResponseTime = (service.avgResponseTime * (service.hits.get() - service.cacheHits.get() - 1) + executionTime)
					/ (service.hits.get() - service.cacheHits.get());
		}

		if (failure) {
			service.failures.incrementAndGet();
		}

		if (error) {
			service.errors.incrementAndGet();
		}
		
	}

}
