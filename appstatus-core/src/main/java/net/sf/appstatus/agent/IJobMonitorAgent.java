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
package net.sf.appstatus.agent;

import net.sf.appstatus.monitor.resource.IJobDetail;

/**
 * Agent dedicated to monitor a job execution.
 * 
 * @author Guillaume Mary
 * 
 */
public interface IJobMonitorAgent {

	/**
	 * Create a new execution step
	 * 
	 * @param jobExecutionId
	 *            job execution id
	 * @param stepMonitor
	 *            step monitor
	 */
	void createNewStepExecution(String jobExecutionId,
			IJobStepMonitorAgent<?> stepMonitor);

	/**
	 * Notify the end of the execution to the agent.
	 * 
	 * @param jobExecutionId
	 *            job execution id
	 */
	void endJobExecution(String idJobExecution);

	/**
	 * Notify the beginning af a job execution.
	 * 
	 * @param jobExecutionId
	 *            current job execution id, if known
	 * @param job
	 *            job detail
	 * @param nbSteps
	 *            total steps
	 * @return job execution id a new job execution id if the paramter
	 *         <code>jobExecutionId</code> is null or blank
	 */
	String startJobExecution(String jobExecutionId, IJobDetail job, int nbSteps);

}
