package net.sf.appstatus.services;

import java.util.concurrent.atomic.AtomicLong;

import net.sf.appstatus.core.services.IService;

import org.apache.commons.lang3.ObjectUtils;

public class Service implements IService {

	public long getRunning() {
		return running.get();
	}

	protected double avgResponseTime = 0;
	protected double avgResponseTimeWithCache = 0;
	protected AtomicLong cacheHits = new AtomicLong();
	protected AtomicLong hits = new AtomicLong();
	protected AtomicLong running = new AtomicLong();
	protected AtomicLong failures = new AtomicLong();
	protected AtomicLong errors = new AtomicLong();
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

	public long getFailures() {
		return failures.get();
	}

	public long getErrors() {
		return errors.get();
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

	public int compareTo(IService otherService) {
		int groupCompare = ObjectUtils.compare(group, otherService.getGroup());
		if (groupCompare != 0) {
			return groupCompare;
		}

		return ObjectUtils.compare(name, otherService.getName());
	}

}
