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
package net.sf.appstatus.core.batch;

import org.slf4j.Logger;

/**
 * Monitor dedicated to monitor a job execution.
 * <p>
 * Usage :
 * 
 * 
 * 
 * @author Guillaume Mary
 * @author Nicolas Richeton
 * 
 */
public interface IBatchProgressMonitor {

	/**
	 * Unknow amount of work.
	 */
	public static final int UNKNOW = -1;

	/**
	 * Begin a task execution.
	 * 
	 * @param name
	 *            task name
	 * @param description
	 *            task description
	 * @param totalWork
	 *            task amount of work to be done
	 */
	void beginTask(String name, String description, int totalWork);

	/**
	 * Create a sub task of this task with the amount of work the subtask
	 * execution will done.
	 * 
	 * @param work
	 *            work units of the task done by the subtask
	 * @return sub task progress monitor
	 */
	IBatchProgressMonitor createSubTask(int work);

	/**
	 * Set the task is done.
	 */
	void done();

	/**
	 * The batch has failed.
	 * 
	 * @param reason
	 */
	void fail(String reason);

	/**
	 * Retrieve the total amount of work for this task.
	 * 
	 * @return the total amount of work
	 */
	int getTotalWork();

	/**
	 * Returns true if cancel has been requested for the current job, usually
	 * from a control UI.
	 * 
	 * @return
	 */
	boolean isCancelRequested();

	/**
	 * Send a message during the task execution.
	 * 
	 * @param message
	 *            message
	 */
	void message(String message);

	/**
	 * Reject one item during the task processing.
	 * 
	 * @param itemId
	 *            rejected item id
	 * @param reason
	 *            the reason
	 */
	void reject(String itemId, String reason);

	void reject(String itemId, String reason, Exception e);

	/**
	 * Reject a set of items during the task processing.
	 * 
	 * @param itemIds
	 *            Array of item ids rejected
	 * @param reason
	 *            the reason
	 */
	void reject(String[] itemIds, String reason);

	void reject(String[] itemIds, String reason, Exception e);

	/**
	 * Set the current item which is being processed.
	 * 
	 * @param item
	 *            current processed item
	 */
	void setCurrentItem(Object item);

	void setLogger(Logger logger);

	/**
	 * Notify the processing of items.
	 * 
	 * @param work
	 *            items processed
	 */
	void worked(int work);
}
