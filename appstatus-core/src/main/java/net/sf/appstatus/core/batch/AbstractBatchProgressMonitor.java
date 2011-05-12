package net.sf.appstatus.core.batch;

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

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log job progress agent.
 * 
 * @author Guillaume Mary
 * @author Nicolas Richeton
 * 
 */
public abstract class AbstractBatchProgressMonitor implements
		IBatchProgressMonitorExt {

	/**
	 * Reference to the batch description
	 */
	private final IBatch batch;

	protected AbstractBatchProgressMonitor currentChild;
	protected int currentChildWork = 0;

	/**
	 * Current item being processed
	 */
	protected Object currentItem;

	protected boolean done = false;

	protected Long endTime = null;
	protected final String executionId;
	private String lastMessage;

	private long lastUpdate = -1;
	private long lastWriteTimestamp;

	private Logger logger = LoggerFactory
			.getLogger(AbstractBatchProgressMonitor.class);

	private String name;

	protected AbstractBatchProgressMonitor parent;

	private int parentWork;
	protected final List<String> rejectedItems = new Vector<String>();

	protected long startTime;

	private boolean success;

	private String taskDescription;

	private String taskGroup;

	protected String taskName;

	protected int totalWork = UNKNOW;

	protected int worked = 0;

	/**
	 * Delay between progress logging. Default to 1s
	 */
	private long writingDelay = 1000;

	/**
	 * Constructor used for main monitor.
	 * 
	 * @param executionId
	 *            job execution id
	 */
	public AbstractBatchProgressMonitor(String executionId, IBatch batch) {
		this.executionId = executionId;
		this.batch = batch;
		this.batch.setProgressMonitor(this);
		startTime = System.currentTimeMillis();
		getLogger().info("[{}] {}: Init",
				new Object[] { this.batch.getGroup(), batch.getName() });
		touch();
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
	protected AbstractBatchProgressMonitor(String executionId,
			IBatchProgressMonitor parent, int parentWork, IBatch batch) {
		this.executionId = executionId;
		this.parent = (AbstractBatchProgressMonitor) parent;
		this.parentWork = parentWork;
		this.batch = batch;
		startTime = System.currentTimeMillis();
		touch();

	}

	/**
	 * {@inheritDoc}
	 */
	public void beginTask(String name, String description, int totalWork) {
		this.name = name;
		this.totalWork = totalWork;
		this.taskName = name;
		this.taskDescription = description;
		getLogger().info(
				"[{}] {}: Begin {} ({}), steps : {}",
				new Object[] { getBatch().getGroup(), getBatch().getName(),
						name, description, String.valueOf(totalWork) });
		touch();

	}

	/**
	 * {@inheritDoc}
	 */
	public IBatchProgressMonitor createSubTask(int work) {
		currentChild = (AbstractBatchProgressMonitor) newInstance(work);
		currentChildWork = work;
		touch();
		return currentChild;
	}

	/**
	 * {@inheritDoc}
	 */
	public void done() {
		endTask(true);

		getLogger().info(
				"[{}] {}: End {}, {} ms",
				new Object[] { getBatch().getGroup(), getBatch().getName(),
						name, System.currentTimeMillis() - startTime });
		touch();

	}

	protected void endBatch(boolean success) {
		getMainMonitor().success = success;
		getMainMonitor().done = true;
		getMainMonitor().endTime = System.currentTimeMillis();
		getMainMonitor().currentItem = null;
	}

	protected void endTask(boolean success) {
		if (parent != null) {
			parent.worked(parentWork);
			parent.currentChild = null;
			parent.currentChildWork = -1;
		}

		currentItem = null;
		done = true;
		this.endTime = System.currentTimeMillis();
		this.success = success;
	}

	public void fail(String reason) {
		message("Failed: " + reason);

		// Mark job as finished
		endTask(false);
		endBatch(false);

		getLogger().info(
				"Failed [{}] {}: {}, duration: {}",
				new Object[] { this.batch.getGroup(), batch.getName(), reason,
						String.valueOf(endTime - startTime) });
		touch();

	}

	public IBatch getBatch() {
		return batch;
	}

	public Object getCurrentItem() {
		return currentItem;
	}

	/**
	 * Returns end date is the task is finished, or an estimate if task is still
	 * running.
	 * 
	 * @return
	 */
	protected Date getEndDate() {

		// If batch is running we try to estimate end date
		if (!done) {

			// Cannot predict end date if progress is unknown
			if (totalWork == UNKNOW) {
				return null;
			}

			long currentTime = System.currentTimeMillis();
			long elapsed = currentTime - startTime;
			long estEndTime = currentTime
					+ (long) (totalWork * elapsed / getProgress());

			return new Date(estEndTime);
		}

		// If batch is done, get the end date
		if (this.endTime != null) {
			return new Date(this.endTime);
		}

		// No end date ?
		return null;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public Date getLastUpdate() {
		return new Date(lastUpdate);
	}

	protected Logger getLogger() {
		return logger;
	}

	protected AbstractBatchProgressMonitor getMainMonitor() {
		AbstractBatchProgressMonitor main = this;

		while (main.parent != null) {
			main = main.parent;
		}

		return main;
	}

	public float getProgress() {
		if (currentChild != null && currentChild.getTotalWork() > 0) {

			float childProgress = currentChildWork * currentChild.getProgress()
					/ currentChild.getTotalWork();

			return worked + childProgress;
		}

		return worked;
	}

	public List<String> getRejectedItems() {
		return rejectedItems;
	}

	protected Date getStartDate() {
		return new Date(startTime);
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public String getTaskGroup() {
		return taskGroup;
	};

	public String getTaskName() {
		return taskName;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTotalWork() {
		return this.totalWork;
	}

	public boolean isCancelRequested() {
		return false;
	}

	public boolean isDone() {
		return done;
	}

	/**
	 * Check if the message is loggable.
	 * 
	 * @param lastWriteTimestamp
	 *            last write timestamp
	 * @return true if the diffrence between the last write time stamp and the
	 *         current is greater than the delay.
	 */
	private boolean isLoggable(long lastWriteTimestamp) {
		if (System.currentTimeMillis() - lastWriteTimestamp > writingDelay) {
			return true;
		}
		return false;
	}

	public boolean isSuccess() {
		return success;
	}

	/**
	 * {@inheritDoc}
	 */
	public void message(String message) {
		getLogger().info("{}: {}", name, message);
		lastMessage = message;
		getMainMonitor().lastMessage = message;
		touch();
	}

	protected abstract IBatchProgressMonitor newInstance(int work);

	/**
	 * {@inheritDoc}
	 */
	public void reject(String itemId, String reason) {
		reject(itemId, reason, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void reject(String itemId, String reason, Exception e) {
		getMainMonitor().rejectedItems.add(itemId);
		getLogger()
				.warn(name + ": rejected " + itemId + " (" + reason + ")", e);
		touch();
	}

	/**
	 * {@inheritDoc}
	 */
	public void reject(String[] itemIds, String reason) {
		reject(itemIds, reason, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void reject(String[] itemIds, String reason, Exception e) {
		if (itemIds != null) {
			for (String id : itemIds) {
				reject(id, reason, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCurrentItem(Object item) {
		currentItem = item;

		if (isLoggable(lastWriteTimestamp)) {
			lastWriteTimestamp = System.currentTimeMillis();
			getLogger().info("{}: working on {}", name, item);
		}
		touch();

	}

	public void setLogger(Logger loggerParam) {
		this.logger = loggerParam;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTotalWork(int totalWork) {
		this.totalWork = totalWork;
	}

	/**
	 * Set the writing delay.
	 * 
	 * @param writingDelay
	 *            writing delay
	 */
	public void setWritingDelay(long writingDelay) {
		this.writingDelay = writingDelay;
	}

	protected void touch() {
		lastUpdate = System.currentTimeMillis();
	}

	/**
	 * {@inheritDoc}
	 */
	public void worked(int work) {
		worked = worked + work;

		if (isLoggable(lastWriteTimestamp)) {
			lastWriteTimestamp = System.currentTimeMillis();
			getLogger().info(
					"[{}] {}: progress {}%",
					new Object[] {
							getBatch().getGroup(),
							getBatch().getName(),
							getMainMonitor().getProgress() * 100f
									/ getMainMonitor().getTotalWork() });
		}
		touch();
	}

}
