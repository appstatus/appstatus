package net.sf.appstatus.services;

import java.util.concurrent.atomic.AtomicLong;

import net.sf.appstatus.core.services.IService;

public class Service implements IService {

	public long getRunning() {
		return running.get();
	}

	

	protected double avgResponseTime = 0;
	protected double avgResponseTimeWithCache = 0;
	protected AtomicLong cacheHits = new AtomicLong();
	protected AtomicLong hits = new AtomicLong();
	protected AtomicLong running = new AtomicLong();
	protected Long maxResponseTime;
	protected Long minResponseTimeWithCache;
	protected Long maxResponseTimeWithCache;
	protected Long minResponseTime;
	protected String name;
	protected String group;

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

	public Double getAvgResponseTime() {
		return avgResponseTime;
	}

	public long getCacheHits() {
		return cacheHits.get();
	}

	public long getHits() {
		return hits.get();
	}

	public Long getMaxResponseTime() {
		return maxResponseTime;
	}

	public Long getMinResponseTime() {
		return minResponseTime;
	}

	public Double getAvgResponseTimeWithCache() {
		return avgResponseTimeWithCache;
	}

	public Long getMaxResponseTimeWithCache() {
		return maxResponseTimeWithCache;
	}

	public Long getMinResponseTimeWithCache() {
		return minResponseTimeWithCache;
	}

}
