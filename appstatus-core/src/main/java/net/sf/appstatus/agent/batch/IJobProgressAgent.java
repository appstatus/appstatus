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

/**
 * Agent dedicated to monitor a job execution.
 * 
 * @author Guillaume Mary
 * 
 */
public interface IJobProgressAgent {

	/**
	 * Unknow amount of work.
	 */
	public static final int UNKNOW = -1;

	/**
	 * Begin a task execution.
	 * 
	 * @param name
	 *            task name
	 * @param group
	 *            task group name
	 * @param description
	 *            task description
	 * @param totalWork
	 *            task amount of work to be done
	 */
	void beginTask(String name, String group, String description, int totalWork);

	/**
	 * Create a sub task of this task with the amount of work the subtask
	 * execution will done.
	 * 
	 * @param work
	 *            work units of the task done by the subtask
	 * @return sub task progress monitor
	 */
	IJobProgressAgent createSubTask(int work);

	/**
	 * Set the task is done.
	 */
	void done();

	/**
	 * Retrieve the total amount of work for this task.
	 * 
	 * @return the total amount of work
	 */
	int getTotalWork();

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
	 * @param item
	 *            rejected item
	 * @param reason
	 *            the reason
	 * @param idMethodName
	 *            name of the method we can use to retrieve the item's id
	 */
	void reject(Object item, String reason, String idMethodName);

	/**
	 * Reject a set of items during the task processing.
	 * 
	 * @param items
	 *            set of items rejected
	 * @param reason
	 *            the reason
	 * @param idMethodName
	 *            name of the method we can use to retrieve the item's id
	 */
	void reject(Object[] items, String reason, String idMethodName);

	/**
	 * Set the current item which are processed.
	 * 
	 * @param item
	 *            current processed item
	 */
	void setCurrentItem(Object item);

	/**
	 * Set the total work amount.
	 * 
	 * @param totalWork
	 *            total work.
	 */
	void setTotalWork(int totalWork);

	/**
	 * Notify the processing of items.
	 * 
	 * @param work
	 *            items processed
	 */
	void worked(int work);
}
