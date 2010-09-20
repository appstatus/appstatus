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
package net.sf.appstatus.agent.service;

import java.util.List;
import java.util.Observer;

/**
 * Service agent monitor interface.
 * 
 * @author Guillaume Mary
 * 
 */
public interface IServiceAgentMonitor extends Observer {
	/**
	 * Return the average flow of the service resource.
	 * 
	 * @param operationName
	 *            operation name of the service
	 * @return the average flow in request by second
	 */
	float getAverageFlow(String operationName);

	/**
	 * Return the average response time of the service resource.
	 * 
	 * @param operationName
	 *            operation name of the service
	 * @return the average response time in millisecond
	 */
	float getAverageResponseTime(String operationName);

	/**
	 * Retrieve the operations names of the service.
	 * 
	 * @return operation names
	 */
	List<String> getOperationNames();
}
