package net.sf.appstatus.services;

import net.sf.appstatus.core.services.IService;

public class Service implements IService {

	public long getRunning() {
		return running;
	}

	public void setRunning(long running) {
		this.running = running;
	}

	public void setCacheHits(long cacheHits) {
		this.cacheHits = cacheHits;
	}

	public void setHits(long hits) {
		this.hits = hits;
	}

	long avrResponseTime;
	long cacheHits;
	long hits;
	long running;
	long maxResponseTime;
	long minResponseTime;
	String name;
	String group;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public long getAvrResponseTime() {
		return avrResponseTime;
	}

	public long getCacheHits() {
		return cacheHits;
	}

	public long getHits() {
		return hits;
	}

	public long getMaxResponseTime() {
		return maxResponseTime;
	}

	public long getMinResponseTime() {
		return minResponseTime;
	}

}
