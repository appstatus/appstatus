/*
 * Copyright 2010 Capgemini Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sf.appstatus.agent.batch;

import java.util.Date;
import java.util.List;
import java.util.Observer;

/**
 * Job progress agent monitor.
 * 
 * @author Guillaume Mary
 * 
 */
public interface IJobProgressAgentMonitor extends Observer {

	/**
	 * Retrieve the end date of the job execution.
	 * 
	 * @param executionId
	 *            execution's id
	 * @return end date of the job execution
	 */
	Date getEndDate(String executionId);

	/**
	 * Retrieve all the jobs execution monitored by this agent monitor.
	 * 
	 * @return list of job execution ids
	 */
	List<String> getJobExecutionIds();

	/**
	 * Retrieve the last messages.
	 * 
	 * @param executionId
	 *            execution id
	 * @param nbMessage
	 *            limit the returned messages
	 * @return list of messages
	 */
	List<String> getLastMessages(String executionId, int nbMessage);

	/**
	 * Retrieve the current progress status of the job execution.
	 * 
	 * @param executionId
	 *            execution id
	 * @return progress value
	 */
	double getProgressStatus(String executionId);

	/**
	 * Return all the rejected items.
	 * 
	 * @param executionId
	 *            job execution id
	 * @return all the rejected items
	 */
	List<String> getRejectedItemsId(String executionId);

	/**
	 * Return the starting date of the job execution.
	 * 
	 * @param executionId
	 *            job execution id
	 * @return start date
	 */
	Date getStartDate(String executionId);

	/**
	 * Retrieve the status of the batch execution.
	 * 
	 * @param executionId
	 *            job execution id.
	 * @return status
	 */
	String getStatus(String executionId);

}
