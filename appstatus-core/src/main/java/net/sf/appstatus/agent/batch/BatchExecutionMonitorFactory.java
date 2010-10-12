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
package net.sf.appstatus.agent.batch;

import net.sf.appstatus.agent.MonitorFactory;

/**
 * Batch Execution Monitor factory.
 * 
 * @author Guillaume Mary
 * 
 */
public final class BatchExecutionMonitorFactory {

	/**
	 * Return a new {@link IBatchExecutionMonitor} instance.
	 * 
	 * @return a new {@link IBatchExecutionMonitor} instance
	 */
	public static IBatchExecutionMonitor getMonitor(String jobName) {
		return getMonitor(MonitorFactory.DEFAULT_MONITOR_NAME, jobName);
	}

	/**
	 * Return a new {@link IBatchExecutionMonitor} instance.
	 * 
	 * @return a new {@link IBatchExecutionMonitor} instance
	 */
	public static IBatchExecutionMonitor getMonitor(String monitorName,
			String jobName) {
		return MonitorFactory.getMonitor(IBatchExecutionMonitor.class,
				monitorName, jobName);
	}

	/**
	 * Default constructor.
	 */
	private BatchExecutionMonitorFactory() {
		// prevent instantiation
	}

}
