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

import java.util.Observable;
import java.util.UUID;

import net.sf.appstatus.agent.service.IServiceAgent;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In memory (cache) service monitor agent and statistics provider.
 * @author Guillaume Mary
 *
 */
public class MemServiceAgent extends Observable implements IServiceAgent {
	
	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory
			.getLogger(MemServiceAgent.class);

	private String serviceName;

	/**
	 * Default constructor.
	 * @param serviceName
	 */
	public MemServiceAgent(String serviceName) {
		this.serviceName = serviceName;
	}
	
	/**
	 * {@inheritDoc} Store the beginning of the service call.
	 */
	public String beginCall(String operationName, Object[] parameters) {
		// retrieve the cache
		Cache cache = getCache(operationName);

		// generate an execution id
		String executionId = UUID.randomUUID().toString();

		ServiceMonitoringData data = new ServiceMonitoringData();
		data.setStartCallTimestamp(System.currentTimeMillis());
		Element elt = new Element(executionId, data);
		cache.put(elt);
		log.info(
				"Start of the service call : <{}.{} ({})>, with parameters : {}",
				new Object[] { serviceName, operationName, executionId,
						parameters });
		return executionId;
	}
	
	/**
	 * Retrieve the call cache for a specified operation.
	 * @param operationName operation name
	 * @return call cache
	 */
	private Cache getCache(String operationName) {
		if (!CacheManager.getInstance().cacheExists(
				serviceName + "." + operationName)) {
			CacheManager.getInstance().addCache(
					serviceName + "." + operationName);
		}
		Cache cache = CacheManager.getInstance().getCache(
				serviceName + "." + operationName);
		return cache;
	}
	
	/**
	 * {@inheritDoc} Store the end of the call.
	 */
	public void endCall(String operationName, String executionId) {
		// retrieve the cache
		Cache cache = getCache(operationName);
		Element elt = cache.get(executionId);
		ServiceMonitoringData data = (ServiceMonitoringData) elt.getValue();
		data.setEndCallTimestamp(System.currentTimeMillis());
		cache.put(elt);
		setChanged();
		notifyObservers(operationName);
		log.info("End of the service call <{}.{} ({})>", new Object[] {
				serviceName, operationName, executionId });
	}
	
	/**
	 * Return the service name.
	 * @return the service name
	 */
	public String getName() {
		return serviceName;
	}
}
