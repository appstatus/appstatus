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
			service.cacheHits++;
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
		service.hits++;
		service.running++;
	}

	public void endCall() {
		endTime = System.currentTimeMillis();

		service.running--;
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
			
			service.avgResponseTimeWithCache = ( service.avgResponseTimeWithCache * (service.cacheHits -1) + response ) / service.cacheHits;
		} else {
			
			if (service.maxResponseTime == null
					|| service.maxResponseTime < response) {
				service.maxResponseTime = response;
			}

			if (service.minResponseTime == null
					|| service.minResponseTime > response) {
				service.minResponseTime = response;
			}
			
			service.avgResponseTime = ( service.avgResponseTime * (service.hits - service.cacheHits -1) + response ) / (service.hits - service.cacheHits);
		}

	}
}
