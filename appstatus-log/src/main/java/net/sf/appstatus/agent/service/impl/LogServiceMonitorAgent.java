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

import java.util.UUID;

import net.sf.appstatus.agent.service.IServiceStatisticsMonitorAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service agent who logs the operation calls.
 * @author Guillaume Mary
 *
 */
public class LogServiceMonitorAgent implements IServiceStatisticsMonitorAgent {

	private static Logger log = LoggerFactory
			.getLogger(LogServiceMonitorAgent.class);

	private String executionId;

	private String serviceName;
	
	/**
	 * Default constructor.
	 * @param serviceName
	 */
	public LogServiceMonitorAgent(String serviceName) {
		this.serviceName = serviceName;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String beginCall(String operationName, Object[] parameters) {
		this.executionId = UUID.randomUUID().toString();
		log.info(
				"Call service <{}.{} ({})>, with these parameters : {}",
				new Object[] { serviceName, operationName, executionId, parameters });
		return executionId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void endCall(String operationName, String executionId) {
		log.info("End of the call of the service <{}.{} ({})>", new Object[] {
				serviceName, operationName, executionId });
	}

}
