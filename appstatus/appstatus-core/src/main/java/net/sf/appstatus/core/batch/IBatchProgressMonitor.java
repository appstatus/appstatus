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

import java.util.Date;

import org.slf4j.Logger;

/**
 * Monitor dedicated to monitor a job execution.
 * <p>
 * Usage :
 * <p>
 * <code>IBatchProgressMonitor monitor, getBatchProgressMonitor("name",
 * 			"group", "uuid");<br/>
 * 			<br/>
 * 			// get item list<br/>
 * 			monitor.beginTask( "taskName", "taskDescr", 2 );<br/>
 * 			monitor.setCurrentItem( "1" ) ;<br/>
 * 			<br/>
 * 			// Do some work on item 1;<br/>
 * 			<br/>
 * 			monitor.worked(1);<br/>
 * 			monitor.setCurrentItem( "2" ) ;<br/>
 * 			<br/>
 * 			// Do some work on item 2;<br/>
 * 			<br/>
 * 			monitor.worked(1);<br/>
 * 			monitor.done();
 * 			
 * </code>
 * 
 * 
 * @author Guillaume Mary
 * @author Nicolas Richeton
 * 
 */
public interface IBatchProgressMonitor {

	/**
	 * Unknown amount of work.
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
	 * Reports batch failure.
	 * 
	 * @param reason
	 */
	void fail(String reason);

	/**
	 * Reports batch failure
	 * <p>
	 * Includes reason and exception if any.
	 * 
	 * @param reason
	 * @param t
	 */
	void fail(String reason, Throwable t);

	Date getLastUpdate();

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

	void reject(String itemId, String reason, Throwable e);

	/**
	 * Reject a set of items during the task processing.
	 * 
	 * @param itemIds
	 *            Array of item ids rejected
	 * @param reason
	 *            the reason
	 */
	void reject(String[] itemIds, String reason);

	void reject(String[] itemIds, String reason, Throwable e);

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
