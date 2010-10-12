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
package net.sf.appstatus.monitor.resource.batch.impl;

import java.util.Map;
import java.util.Set;

import net.sf.appstatus.IPropertyProvider;
import net.sf.appstatus.IStatusChecker;
import net.sf.appstatus.agent.batch.BatchExecutionMonitorFactory;
import net.sf.appstatus.agent.batch.IBatchExecutionMonitor;
import net.sf.appstatus.monitor.resource.impl.BasicStatusResourceMonitor;

/**
 * Status job resource.
 * 
 * @author Guillaume Mary
 * 
 */
public class BatchStatusResourceMonitor extends BasicStatusResourceMonitor {

	private final IBatchExecutionMonitor batchExecutionMonitor;

	public BatchStatusResourceMonitor(String id, String name, String type,
			Set<IStatusChecker> statusCheckers,
			Set<IPropertyProvider> propertyProviders,
			String executionMonitorName, String historyMonitorName,
			String nextMonitorName) {
		super(id, name, type, statusCheckers, propertyProviders);
		if (executionMonitorName != null) {
			this.batchExecutionMonitor = BatchExecutionMonitorFactory
					.getMonitor(executionMonitorName, id);
		} else {
			this.batchExecutionMonitor = BatchExecutionMonitorFactory
					.getMonitor(id);
		}
	}

	@Override
	public Map<String, Map<String, String>> getDetails() {
		// TODO Auto-generated method stub
		return super.getDetails();
	}
}
