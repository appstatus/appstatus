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
package net.sf.appstatus.agent.service.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import net.sf.appstatus.agent.service.IServiceStatisticsMonitor;

/**
 * A direct NOP (no operation) implementation of {@link IServiceStatisticsMonitor}.
 * 
 * @author Guillaume Mary
 * 
 */
public class NOPServiceStatisticsMonitor implements IServiceStatisticsMonitor {

	/**
	 * The unique instance of NOPServiceAgentMonitor.
	 */
	public static final NOPServiceStatisticsMonitor NOP_SERVICE_AGENT_MONITOR = new NOPServiceStatisticsMonitor();

	private static final List<String> NOP_OPERATION_NAMES = new ArrayList<String>();

	/**
	 * There is no point in creating multiple instances of
	 * NOPServiceMonitorStatisticsProvider, except by derived classes, hence the
	 * protected access for the constructor.
	 */
	protected NOPServiceStatisticsMonitor() {
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

	/**
	 * {@inheritDoc}
	 */
	public void update(Observable o, Object arg) {
		// NOP
	}

}
