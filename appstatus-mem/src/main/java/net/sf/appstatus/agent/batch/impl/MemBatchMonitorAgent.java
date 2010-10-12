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
package net.sf.appstatus.agent.batch.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Observable;

import net.sf.appstatus.agent.batch.IBatchExecutionMonitorAgent;
import net.sf.appstatus.monitor.resource.batch.JobStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log job progress monitor agent.
 * 
 * @author Guillaume Mary
 * 
 */
public class MemBatchMonitorAgent extends Observable implements
IBatchExecutionMonitorAgent, Serializable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 1533529143425265481L;

	private static final String UNKNOW_ID = "unknow-id";

	public static final String BEGIN_TASK_ACTION = "beginTask";

	public static final String END_TASK_ACTION = "endTask";

	private static final long DEFAULT_WRITING_DELAY = 5000;

	private static Logger logger = LoggerFactory
			.getLogger(MemBatchMonitorAgent.class);

	private final String executionId;

	private Object currentItem;

	private long lastMessageStoreTimestamp;

	private String name;

	private MemBatchMonitorAgent parent;

	private int parentWork;

	private final List<RejectedItem> rejectedItems;

	private final Deque<String> messages;

	private long startTimestamp;

	private int totalWork;

	private int worked;

	private long storeDelay = DEFAULT_WRITING_DELAY;

	private long endTimestamp;

	private String status = JobStatus.UNKNOW.getLabel();

	private final List<MemBatchMonitorAgent> subTasks = new ArrayList<MemBatchMonitorAgent>();

	/**
	 * Default constructor.
	 * 
	 * @param executionId
	 *            job execution id
	 */
	public MemBatchMonitorAgent(String executionId) {
		this.executionId = executionId;
		this.rejectedItems = new ArrayList<RejectedItem>();
		this.messages = new ArrayDeque<String>();
	}

	/**
	 * Private constructor used to create a sub task.
	 * 
	 * @param executionId
	 *            execution id
	 * @param parent
	 *            parent monitor
	 * @param parentWork
	 *            parent amount of work
	 */
	private MemBatchMonitorAgent(String executionId, MemBatchMonitorAgent parent,
			int parentWork) {
		this.executionId = executionId;
		this.parent = parent;
		this.parentWork = parentWork;
		this.rejectedItems = new ArrayList<RejectedItem>();
		this.messages = new ArrayDeque<String>();
	}

	/**
	 * {@inheritDoc}
	 */
	public void beginTask(String name, String group, String description,
			int totalWork) {
		this.name = name;
		this.totalWork = totalWork;
		this.startTimestamp = System.currentTimeMillis();
		this.status = JobStatus.STARTED.getLabel();
		setChanged();
		notifyObservers(BEGIN_TASK_ACTION);
		logger.info(
				"Start task <{}>-<{}> ({}), id : {} timstamp : {}, totalWork : {}",
				new Object[] { group, name, description, executionId,
						startTimestamp, String.valueOf(totalWork) });
	}

	/**
	 * {@inheritDoc}
	 */
	public IBatchExecutionMonitorAgent createSubTask(int work) {
		MemBatchMonitorAgent subTask = new MemBatchMonitorAgent(executionId,
				this, work);
		subTasks.add(subTask);
		return subTask;
	}

	/**
	 * {@inheritDoc}
	 */
	public void done() {
		if (parent != null) {
			parent.worked(parentWork);
		}
		this.endTimestamp = System.currentTimeMillis();
		this.status = JobStatus.COMPLETED.getLabel();
		setChanged();
		notifyObservers(END_TASK_ACTION);
		logger.info(
				"End task <{}> [totalTime : {} ms, total items : {}, processed : {}, rejected : {}]",
				new Object[] { name,
						System.currentTimeMillis() - startTimestamp, totalWork,
						worked, rejectedItems.size() });
	}

	public Object getCurrentItem() {
		return currentItem;
	}

	public long getEndTimestamp() {
		return endTimestamp;
	}

	public String getExecutionId() {
		return executionId;
	}

	public Deque<String> getMessages() {
		return messages;
	}

	public String getName() {
		return name;
	}

	public MemBatchMonitorAgent getParent() {
		return parent;
	}

	public int getParentWork() {
		return parentWork;
	}

	public List<RejectedItem> getRejectedItems() {
		return rejectedItems;
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public String getStatus() {
		return status;
	}

	public List<MemBatchMonitorAgent> getSubTasks() {
		return subTasks;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTotalWork() {
		return this.totalWork;
	}

	public int getWorked() {
		return worked;
	}

	/**
	 * Check if the message is storable.
	 * 
	 * @param lastStoreTimestamp
	 *            last store timestamp
	 * @return true if the diffrence between the last store time stamp and the
	 *         current is greater than the delay.
	 */
	private boolean isStorable(long lastStoreTimestamp) {
		if (System.currentTimeMillis() - lastStoreTimestamp > storeDelay) {
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void message(String message) {
		if (isStorable(lastMessageStoreTimestamp)) {
			lastMessageStoreTimestamp = System.currentTimeMillis();
			messages.push(message);
			setChanged();
			notifyObservers();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void reject(Object item, String reason, String idMethodName) {
		String id = retrieveIdFromItem(item, idMethodName);
		rejectedItems.add(new RejectedItem(id, item, reason));
	}

	/**
	 * {@inheritDoc}
	 */
	public void reject(Object[] items, String reason, String idMethodName) {
		for (Object item : items) {
			String id = retrieveIdFromItem(item, idMethodName);
			rejectedItems.add(new RejectedItem(id, item, reason));
		}
	}

	private String retrieveIdFromItem(Object item, String idMethodName) {
		String id = UNKNOW_ID;
		if (idMethodName != null) {
			try {
				Method getIdMethod = item.getClass().getMethod(idMethodName,
						(Class<?>[]) null);
				id = getIdMethod.invoke(item, (Object[]) null).toString();
			} catch (SecurityException e) {
				logger.info(
						"Can't access to the method {} of item {}. We can't retrieve the item's id, it will be set to the unknow value.",
						e, new Object[] { idMethodName, item });
			} catch (NoSuchMethodException e) {
				logger.info(
						"The method {} of item {} doesn't exist. We can't retrieve the item's id, it will be set to the unknow value.",
						e, new Object[] { idMethodName, item });
			} catch (IllegalArgumentException e) {
				logger.info(
						"The method {} of item {} needs parameters. We can't retrieve the item's id, it will be set to the unknow value.",
						e, new Object[] { idMethodName, item });
			} catch (IllegalAccessException e) {
				logger.info(
						"Can't access to the method {} of item {}. We can't retrieve the item's id, it will be set to the unknow value.",
						e, new Object[] { idMethodName, item });
			} catch (InvocationTargetException e) {
				logger.info(
						"Error during the invocation of the method {} of item {}. We can't retrieve the item's id, it will be set to the unknow value.",
						e, new Object[] { idMethodName, item });
			}
		}
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCurrentItem(Object item) {
		this.currentItem = item;
	}

	/**
	 * Set the store delay.
	 * 
	 * @param storeDelay
	 *            store delay
	 */
	public void setStoreDelay(long storeDelay) {
		this.storeDelay = storeDelay;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTotalWork(int totalWork) {
		this.totalWork = totalWork;
	}

	/**
	 * {@inheritDoc}
	 */
	public void worked(int work) {
		worked = worked + work;
		setChanged();
		notifyObservers();
	}

}
