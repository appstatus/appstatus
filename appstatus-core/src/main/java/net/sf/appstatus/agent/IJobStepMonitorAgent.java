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

import net.sf.appstatus.error.IBatchProcessError;

/**
 * Step monitor agent.
 * 
 * @author Guillaume Mary
 * 
 * @param <T>
 *            step class
 */
public interface IJobStepMonitorAgent<T> {

	/**
	 * Notify the end of the step execution to the agent.
	 * 
	 * @param idJobExecution
	 *            job execution id
	 * @param idStepExecution
	 *            step execution id
	 */
	void endStepExecution();

	/**
	 * Monitor a process error which occur during the step execution.
	 * 
	 * @param idJobExecution
	 *            job execution id
	 * @param idStepExecution
	 *            step execution id
	 * @param error
	 *            error informations
	 */
	void monitorProcessError(IBatchProcessError error);

	/**
	 * Notify the step execution's start.
	 */
	void startStepExecution();

	/**
	 * Notify the number of the processed items to the agent.
	 * 
	 * @param nbItems
	 *            total items processed
	 */
	void worked(int nbItems);

}
