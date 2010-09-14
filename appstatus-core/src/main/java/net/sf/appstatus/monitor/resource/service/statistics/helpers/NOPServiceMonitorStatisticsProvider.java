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
package net.sf.appstatus.monitor.resource.service.statistics.helpers;

import java.util.ArrayList;
import java.util.List;

import net.sf.appstatus.monitor.resource.service.statistics.IServiceMonitorStatisticsProvider;

/**
 * A direct NOP (no operation) implementation of
 * {@link IServiceMonitorStatisticsProvider}.
 * 
 * @author Guillaume Mary
 * 
 */
public class NOPServiceMonitorStatisticsProvider implements
		IServiceMonitorStatisticsProvider {

	/**
	 * The unique instance of NOPServiceMonitorStatisticsProvider.
	 */
	public static final NOPServiceMonitorStatisticsProvider NOP_SERVICE_MONITOR_AGENT = new NOPServiceMonitorStatisticsProvider();

	private static final List<String> NOP_OPERATION_NAMES = new ArrayList<String>();

	/**
	 * There is no point in creating multiple instances of
	 * NOPServiceMonitorStatisticsProvider, except by derived classes, hence the
	 * protected access for the constructor.
	 */
	protected NOPServiceMonitorStatisticsProvider() {
	}

	/**
	 * {@inheritDoc}
	 */
	public float getAverageFlow(String operationName) {
		// NOP
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public float getAverageResponseTime(String operationName) {
		// NOP
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getOperationNames() {
		return NOP_OPERATION_NAMES;
	}

}
