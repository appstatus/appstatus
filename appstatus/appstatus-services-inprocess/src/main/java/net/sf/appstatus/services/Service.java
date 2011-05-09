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

	protected double avgResponseTime = 0;
	protected double avgResponseTimeWithCache = 0;
	protected long cacheHits;
	protected long hits;
	protected long running;
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
		return cacheHits;
	}

	public long getHits() {
		return hits;
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
