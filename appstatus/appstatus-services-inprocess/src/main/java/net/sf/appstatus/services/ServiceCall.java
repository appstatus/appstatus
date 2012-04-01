package net.sf.appstatus.services;

import net.sf.appstatus.core.services.IServiceMonitor;

public class ServiceCall implements IServiceMonitor {

	String id;
	long startTime;
	boolean cacheHit;
	Long endTime = null;
	Service service;
	Object[] parameters;

	public void cacheHit() {
		if (!this.cacheHit) {
			this.cacheHit = true;
			service.cacheHits.incrementAndGet();
		}
	}

	public ServiceCall(Service service) {
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
		this.parameters = parameters;
		service.hits.incrementAndGet();
		service.running.incrementAndGet();
	}
 
	public void endCall() {
		if (endTime != null)	{
			// endCall was called twice ! returning directly.
			return;
		}
		
		endTime = System.currentTimeMillis();
		service.running.decrementAndGet();

		long response = endTime - startTime;

		if (cacheHit) {
			if (service.maxResponseTimeWithCache == null
					|| service.maxResponseTimeWithCache < response) {
				service.maxResponseTimeWithCache = response;
			}

			if (service.minResponseTimeWithCache == null
					|| service.minResponseTimeWithCache > response) {
				service.minResponseTimeWithCache = response;
			}

			service.avgResponseTimeWithCache = (service.avgResponseTimeWithCache
					* (service.cacheHits.get() - 1) + response)
					/ service.cacheHits.get();
		} else {

			if (service.maxResponseTime == null
					|| service.maxResponseTime < response) {
				service.maxResponseTime = response;
			}

			if (service.minResponseTime == null
					|| service.minResponseTime > response) {
				service.minResponseTime = response;
			}

			service.avgResponseTime = (service.avgResponseTime
					* (service.hits.get() - service.cacheHits.get() - 1) + response)
					/ (service.hits.get() - service.cacheHits.get());
		}

	}
}
