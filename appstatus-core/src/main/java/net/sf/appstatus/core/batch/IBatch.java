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
package net.sf.appstatus.core.batch;

import java.util.Date;
import java.util.List;

/**
 * Job progress agent monitor.
 * 
 * @author Guillaume Mary
 * 
 */
public interface IBatch {
	String STATUS_FAILURE = "failure";
	String STATUS_RUNNING = "running";
	String STATUS_SUCCESS = "success";

	String getCurrentItem();

	String getCurrentTask();

	/**
	 * Retrieve the end date of the job execution.
	 * 
	 * @param executionId
	 *            execution's id
	 * @return end date of the job execution
	 */
	Date getEndDate();

	String getGroup();

	long getItemCount();

	/**
	 * Retrieve the last messages.
	 * 
	 * @param executionId
	 *            execution id
	 * @param nbMessage
	 *            limit the returned messages
	 * @return list of messages
	 */
	String getLastMessage();

	Date getLastUpdate();

	String getName();

	/**
	 * Retrieve the current progress status of the job execution.
	 * 
	 * @param executionId
	 *            execution id
	 * @return progress value
	 */
	float getProgressStatus();

	/**
	 * Return all the rejected items.
	 * 
	 * @param executionId
	 *            job execution id
	 * @return all the rejected items
	 */
	List<String> getRejectedItemsId();

	/**
	 * Return the starting date of the job execution.
	 * 
	 * @param executionId
	 *            job execution id
	 * @return start date
	 */
	Date getStartDate();

	/**
	 * Retrieve the status of the batch execution.
	 * 
	 * @param executionId
	 *            job execution id.
	 * @return status
	 */
	String getStatus();

	/**
	 * Retrieve all the jobs execution monitored by this agent monitor.
	 * 
	 * @return list of job execution ids
	 */
	String getUuid();

	boolean isSuccess();

	/**
	 * This method is not intended to be used directly.
	 * 
	 * @param monitor
	 */
	void setProgressMonitor(IBatchProgressMonitor monitor);
}
