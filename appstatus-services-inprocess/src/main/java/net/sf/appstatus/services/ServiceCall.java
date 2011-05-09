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

	public void beginCall(Object[] parameters) {
		this.parameters = parameters;
		service.hits++;
		service.running++;
	}

	public void endCall() {
		endTime = System.currentTimeMillis();

		// don't store parameters
		this.parameters = null;
		service.running--;

	}
}
