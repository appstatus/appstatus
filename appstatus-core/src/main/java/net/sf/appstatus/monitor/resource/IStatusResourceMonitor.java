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
package net.sf.appstatus.monitor.resource;

import java.util.List;
import java.util.Map;

import net.sf.appstatus.IStatusResult;

/**
 * @author Guillaume Mary
 * 
 */
public interface IStatusResourceMonitor {

	/**
	 * Retrieve the resource configuration
	 * 
	 * @return resource configuration, return null if there is no property
	 *         providers configured.
	 */
	Map<String, Map<String, String>> getConfiguration();

	/**
	 * Retrieve the resource details
	 * 
	 * @return resource details, return null for the basic resource.
	 */
	Map<String, Map<String, String>> getDetails();

	/**
	 * Return the resource unique identifier
	 * 
	 * @return
	 */
	String getId();

	/**
	 * Return the resource name
	 * 
	 * @return resource name.
	 */
	String getName();

	/**
	 * Return the list of status of the resource return by the probe.
	 * 
	 * @return status results, return null if there is no status checkers
	 *         configured.
	 */
	List<IStatusResult> getStatus();

	/**
	 * Return the resource name.
	 * 
	 * @return the resource type
	 */
	String getType();
}
