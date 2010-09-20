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
package net.sf.appstatus.monitor.resource.batch;

import java.util.Date;
import java.util.List;

import net.sf.appstatus.monitor.resource.IStatusResource;

/**
 * Status of a job execution.
 * 
 * @author Guillaume Mary
 * 
 */
public interface IStatusJobExecutionResource extends IStatusResource {
	/**
	 * Return the job's execution end date.
	 * 
	 * @return execution end date
	 */
	Date getEndDate();

	/**
	 * Return the job status
	 * 
	 * @return
	 */
	String getJobStatus();

	/**
	 * Return the last message of the batch execution.
	 * 
	 * @param nbMessage
	 *            number of message to read
	 * @return last message list
	 */
	List<String> getLastMessages(int nbMessage);

	/**
	 * Return the progress status.
	 * 
	 * @return progress status
	 */
	double getProgressStatus();

	/**
	 * Return the rejected item ids.
	 * 
	 * @return rejected item ids
	 */
	List<String> getRejectedItemsId();

	/**
	 * Return the job's execution start date.
	 * 
	 * @return execution start date
	 */
	Date getStartDate();

}
