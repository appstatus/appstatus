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
     * Set the task is done with custom status.
     * 
     * @param customStatus
     */
    void done(String customStatus);

    /**
     * Report global failure.
     * <p>
     * Use this when the batch has issued a major error and cannot continue.
     *
     * @param reason
     */
    void fail(String reason);

    /**
     * Report global failure.
     * <p>
     * Use this when the batch has issued a major error and cannot continue.
     *
     * @param reason
     * @param t
     *            Exception which caused the failure.
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
     * <p>
     * Use this when :
     * <ul>
     * <li>Item processing has failed (exception, invalid data, connection issue
     * ..) and a human action is probably required to before retry.</li>
     * <li>The batch is able to go on and process other items (item processing
     * is independent).</li>
     * </ul>
     * <p>
     *
     * @see IBatchProgressMonitor#reject(String, String, Throwable)
     *      IBatchProgressMonitor#reject(String, String, Throwable) can be used
     *      to provide the exception which has caused the failure.
     *
     * @param itemId
     *            rejected item id
     * @param reason
     *            the reason
     */
    void reject(String itemId, String reason);

    /**
     * * Reject one item during the task processing.
     *
     * @see #reject(String, String)
     * @param itemId
     * @param reason
     * @param e
     *            Exception
     */
    void reject(String itemId, String reason, Throwable e);

    /**
     * Reject a set of items during the task processing.
     *
     * @see #reject(String, String)
     *
     *
     * @param itemIds
     *            Array of item ids rejected
     * @param reason
     *            the reason
     */
    void reject(String[] itemIds, String reason);

    /**
     * Reject a set of items during the task processing.
     *
     * @see #reject(String, String)
     * @param itemIds
     * @param reason
     * @param e
     *            Exception
     */
    void reject(String[] itemIds, String reason, Throwable e);

    /**
     * Set the current item which is being processed.
     *
     * @param item
     *            current processed item
     */
    void setCurrentItem(Object item);

    /**
     * Set the logger to use for the current batch.
     *
     * @param logger
     */
    void setLogger(Logger logger);

    /**
     * Notify the processing of items.
     *
     * @param work
     *            items processed
     */
    void worked(int work);
}
