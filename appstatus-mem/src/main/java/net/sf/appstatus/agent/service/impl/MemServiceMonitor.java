/*
 * Copyright 2010 Capgemini
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 */
package net.sf.appstatus.agent.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.appstatus.agent.service.IServiceStatisticsMonitorAgent;
import net.sf.appstatus.agent.service.IServiceStatisticsMonitor;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Memory service agent monitor.
 * 
 * @author Guillaume Mary
 * 
 */
public class MemServiceMonitor implements IServiceStatisticsMonitor {

	private final String serviceName;

	private final Map<String, Float> averageFlows = new ConcurrentHashMap<String, Float>();

	private final Map<String, Float> averageResponseTimes = new ConcurrentHashMap<String, Float>();

	private static int dataProcessingDelay = 1000;

	private long lastDataProcessingTimestamp;

	private final List<String> operationNames = new ArrayList<String>();

	public MemServiceMonitor(String serviceName) {
		this.serviceName = serviceName;
	}

	private void addOperationName(String operationName) {
		// eventually add the operation to the list
		if (!operationNames.contains(operationName)) {
			operationNames.add(operationName);
		}
	}

	private void calculateAverageFlows() {
		Float averageFlow;
		// retrieve all the operations
		for (String operationName : getOperationNames()) {
			Cache cache = CacheManager.getInstance().getCache(
					serviceName + "." + operationName);
			averageFlow = new Float(0);
			if (cache != null) {
				List<ServiceMonitoringData> datas = getOperationCallsData(cache);
				long period = cache.getCacheConfiguration()
						.getTimeToLiveSeconds();
				averageFlow = (float) datas.size() / period;
			}
			this.averageFlows.put(operationName, averageFlow);
		}
	}

	private void calculateAverageResponseTime() {
		Float averageResponseTime;
		// retrieve all the operations
		for (String operationName : getOperationNames()) {
			Cache cache = CacheManager.getInstance().getCache(
					serviceName + "." + operationName);
			if (cache != null) {
				List<ServiceMonitoringData> datas = getOperationCallsData(cache);
				int nbCalls = 0;
				long totalResponseTime = 0;
				averageResponseTime = new Float(0);
				if (!datas.isEmpty()) {
					for (ServiceMonitoringData data : datas) {
						if (data.getEndCallTimestamp() != 0
								&& data.getStartCallTimestamp() != 0) {
							totalResponseTime = totalResponseTime
									+ (data.getEndCallTimestamp() - data
											.getStartCallTimestamp());
							nbCalls++;
						}
					}
					if (totalResponseTime != 0) {
						averageResponseTime = (float) nbCalls
								/ totalResponseTime;
					}
				}
				this.averageResponseTimes.put(operationName,
						averageResponseTime);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public float getAverageFlow(String operationName) {
		return this.averageFlows.get(operationName);
	}

	/**
	 * {@inheritDoc}
	 */
	public float getAverageResponseTime(String operationName) {
		return this.averageResponseTimes.get(operationName);
	}

	/**
	 * Get all the non expired calls data.
	 * 
	 * @param cache
	 *            the cache
	 * @return the all non expired calls data
	 */
	private List<ServiceMonitoringData> getOperationCallsData(Cache cache) {
		List<ServiceMonitoringData> datas = new ArrayList<ServiceMonitoringData>();
		// retrieve the operation calls that are not expired
		@SuppressWarnings("unchecked")
		List<Object> keys = cache.getKeysWithExpiryCheck();
		for (Object key : keys) {
			Element elt = cache.get(key);
			if (elt != null && !elt.isExpired()) {
				datas.add((ServiceMonitoringData) elt.getValue());
			}
		}
		return datas;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getOperationNames() {
		return operationNames;
	}

	private boolean isDataProcessingRequired() {
		long currentTimestamp = System.currentTimeMillis();
		if (currentTimestamp - lastDataProcessingTimestamp >= dataProcessingDelay) {
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(Observable o, Object arg) {
		if (o instanceof IServiceStatisticsMonitorAgent) {
			if (arg instanceof String) {
				addOperationName((String) arg);
			}
			if (isDataProcessingRequired()) {
				calculateAverageFlows();
				calculateAverageResponseTime();
			}
		}
	}

}
