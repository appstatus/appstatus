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
package net.sf.appstatus.monitor.resource.service;

import net.sf.appstatus.monitor.resource.IStatusResource;

/**
 * 
 * @author Guillaume Mary
 * 
 */
public interface IStatusServiceResource extends IStatusResource {

	/**
	 * Return the average flow of the service resource.
	 * 
	 * @return the average flow in request by second
	 */
	int getAverageFlow();

	/**
	 * Return the average response time of the service resource.
	 * 
	 * @return the average response time in millisecond
	 */
	long getAverageResponseTime();

}
