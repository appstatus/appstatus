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

	public ServiceCall(Service service, boolean log) {
		super(service, log);
		this.service = service;
		startTime = System.currentTimeMillis();
	}

	public String getId() {
		return id;
	}

	public long getStartTime() {
		return startTime;
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
		long response = endTime - startTime;
		if (cacheHit) {
			if (service.maxResponseTimeWithCache == null || service.maxResponseTimeWithCache < response) {
				service.maxResponseTimeWithCache = response;
			}

			if (service.minResponseTimeWithCache == null || service.minResponseTimeWithCache > response) {
				service.minResponseTimeWithCache = response;
			}

			service.avgResponseTimeWithCache = (service.avgResponseTimeWithCache * (service.cacheHits.get() - 1) + response)
					/ service.cacheHits.get();
		} else {

			if (service.maxResponseTime == null || service.maxResponseTime < response) {
				service.maxResponseTime = response;
			}

			if (service.minResponseTime == null || service.minResponseTime > response) {
				service.minResponseTime = response;
			}

			service.avgResponseTime = (service.avgResponseTime * (service.hits.get() - service.cacheHits.get() - 1) + response)
					/ (service.hits.get() - service.cacheHits.get());
		}

		if (failure)
			service.failures.incrementAndGet();

		if (error) {

		}

	}

}
