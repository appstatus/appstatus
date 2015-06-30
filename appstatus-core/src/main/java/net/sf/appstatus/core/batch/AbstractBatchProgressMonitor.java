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
public abstract class AbstractBatchProgressMonitor implements IBatchProgressMonitorExt {

	/**
	 * Reference to the batch description
	 */
	private final IBatch batch;

	protected List<AbstractBatchProgressMonitor> currentChildren;

	/**
	 * Current item being processed
	 */
	protected Object currentItem;
	protected boolean done = false;

	protected Long endTime = null;

	protected final String executionId;
	protected long itemCount = 0;
	private String lastMessage;

	private long lastUpdate = -1;
	private long lastWriteTimestamp;

	private Logger logger = LoggerFactory.getLogger("batch");

	private String name;

	protected AbstractBatchProgressMonitor parent;

	private int parentWork;
	protected final List<String> rejectedItems = new Vector<String>();
	protected long startTime;

	/**
	 * Batch execution status. true if batch terminated normaly.
	 */
	private boolean success;

	protected final List<String> successItems = new Vector<String>();

	private String taskDescription;

	private String taskGroup;

	/**
	 * Name of the task, set with beginTask
	 * <p>
	 * Init to null : cannot be set multiple times. beginTask will fail if
	 * taskName is not null.
	 */
	protected String taskName = null;

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
		this.currentChildren = new Vector<AbstractBatchProgressMonitor>();
		this.batch.setProgressMonitor(this);
		startTime = System.currentTimeMillis();
		getLogger().info("[{}] {}: Init progress monitoring (id: {})",
				new Object[] { this.batch.getGroup(), batch.getName(), batch.getUuid() });
		touch();
	}

	/**
	 * Protected constructor used to create a sub task.
	 *
	 * @param executionId
	 *            execution id
	 * @param parent
	 *            parent monitor
	 * @param parentWork
	 *            parent amount of work
	 */
	protected AbstractBatchProgressMonitor(String executionId, IBatchProgressMonitor parent, int parentWork,
			IBatch batch) {
		this.executionId = executionId;
		this.parent = (AbstractBatchProgressMonitor) parent;
		this.parentWork = parentWork;
		this.currentChildren = new Vector<AbstractBatchProgressMonitor>();
		this.batch = batch;
		startTime = System.currentTimeMillis();
		touch();

	}

	/**
	 * {@inheritDoc}
	 */
	public void beginTask(String name, String description, int totalWork) {
		// This method can only be called one.
		if (this.taskName != null) {
			throw new IllegalStateException("beginTask can only be called once (" + name + ", " + description + ", "
					+ totalWork + ")");
		}
		this.totalWork = totalWork;
		this.taskName = name;
		this.taskDescription = description;
		getLogger().info(
				"[{}] {}: Begin {} ({}), steps : {}",
				new Object[] { getBatch().getGroup(), getBatch().getName(), name, description,
						String.valueOf(totalWork) });
		touch();

	}

	/**
	 * {@inheritDoc}
	 */
	public IBatchProgressMonitor createSubTask(int work) {
		AbstractBatchProgressMonitor newSubTask = (AbstractBatchProgressMonitor) newInstance(work);
		newSubTask.setLogger(logger);
		currentChildren.add(newSubTask);
		touch();
		return newSubTask;
	}

	/**
	 * {@inheritDoc}
	 */
	public void done() {
		if (parent != null) {
			endTask(true);
			getLogger().info(
					"[{}] {}: End {}, {} ms",
					new Object[] { getBatch().getGroup(), getBatch().getName(), name,
							System.currentTimeMillis() - startTime });
		} else {
			endBatch(true);
			getLogger()
			.info("[{}] {}: End batch, {} ms",
					new Object[] { getBatch().getGroup(), getBatch().getName(),
					System.currentTimeMillis() - startTime });

		}

		touch();

	}

	protected void endBatch(boolean success) {
		getMainMonitor().success = success;
		getMainMonitor().done = true;
		getMainMonitor().endTime = System.currentTimeMillis();
		getMainMonitor().currentItem = null;

		if (getMainMonitor().totalWork != UNKNOW && getMainMonitor().worked < getMainMonitor().totalWork) {
			getMainMonitor().worked = getMainMonitor().totalWork;
		}
		getMainMonitor().currentChildren.clear();

		onBatchEnd();
	}

	protected void endTask(boolean success) {

		done = true;
		currentItem = null;
		this.endTime = System.currentTimeMillis();
		this.success = success;

		if (parent != null) {
			parent.worked(parentWork);
			parent.currentChildren.remove(this);
		}

	}

	public void fail(String reason) {
		fail(reason, null);

	}

	public void fail(String reason, Throwable t) {
		message("Failed: " + reason + " " + (t != null ? t.getMessage() : ""));

		// Mark job as finished and failure
		endTask(false);
		endBatch(false);

		getLogger().error("[{}] {}: FAILED ({}) , duration: {} ms", this.batch.getGroup(), batch.getName(), reason,
				String.valueOf(endTime - startTime), t);
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
			long estEndTime = currentTime + (long) (totalWork * elapsed / getProgress());

			return new Date(estEndTime);
		}

		// If batch is done, get the end date
		if (this.endTime != null) {
			return new Date(this.endTime);
		}

		// No end date ?
		return null;
	}

	public long getItemCount() {
		return itemCount;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public Date getLastUpdate() {
		return new Date(getMainMonitor().lastUpdate);
	}

	protected Logger getLogger() {
		return getMainMonitor().logger;
	}

	/**
	 * Get the top parent.
	 *
	 * @return
	 */
	protected AbstractBatchProgressMonitor getMainMonitor() {
		AbstractBatchProgressMonitor main = this;

		while (main.parent != null) {
			main = main.parent;
		}

		return main;
	}

	protected int getParentWork() {
		return parentWork;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.sf.appstatus.core.batch.IBatchProgressMonitorExt#getProgress()
	 */
	public float getProgress() {

		if (totalWork == UNKNOW) {
			return UNKNOW;
		}

		float result = worked;

		for (AbstractBatchProgressMonitor child : currentChildren) {

			if (child.getTotalWork() != UNKNOW && !child.isDone()) {
				float childProgress = child.getParentWork() * child.getProgress() / child.getTotalWork();
				result = result + childProgress;
			}

		}

		return result;
	}

	/**
	 * Get the list of rejected items.
	 *
	 * @return empty list or list of rejected items.
	 */
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
		String actionName = name;
		if (name == null) {
			actionName = batch.getName();
		}

		getLogger().info("[{}] {}: {} ## {}", getBatch().getGroup(), getBatch().getName(), actionName, message);
		lastMessage = message;
		getMainMonitor().lastMessage = message;
		touch();
	}

	protected abstract IBatchProgressMonitor newInstance(int work);

	protected void onBatchEnd() {

	}

	/**
	 * {@inheritDoc}
	 */
	public void reject(String itemId, String reason) {
		reject(itemId, reason, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void reject(String itemId, String reason, Throwable e) {
		getMainMonitor().rejectedItems.add(itemId);
		getLogger().warn("[{}] {}: {} rejected {} ({})", getBatch().getGroup(), getBatch().getName(), name, itemId,
				reason, e);

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
	public void reject(String[] itemIds, String reason, Throwable e) {
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
		if (currentItem != item) {
			getMainMonitor().itemCount++;
		}

		currentItem = item;

		if (isLoggable(lastWriteTimestamp)) {
			lastWriteTimestamp = System.currentTimeMillis();
			String actionName = name;
			if (actionName == null) {
				actionName = batch.getName();
			}
			getLogger().info("[{}] {}: {} working on {} (#{})",
					new Object[] { getBatch().getGroup(), getBatch().getName(), actionName, item, itemCount });
		}
		touch();

	}

	public void setLogger(Logger loggerParam) {
		getMainMonitor().logger = loggerParam;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTotalWork(int totalWork) {
		if (totalWork < 0) {
			throw new IllegalArgumentException("totalWork cannot be negative : " + totalWork);
		}

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

	/**
	 * Updated 'lastUpdate' value.
	 */
	protected void touch() {
		getMainMonitor().lastUpdate = System.currentTimeMillis();
	}

	/**
	 * {@inheritDoc}
	 */
	public void worked(int work) {

		if (work < 0) {
			throw new IllegalArgumentException("work cannot be negative : " + work);
		}
		worked = worked + work;

		// Ensure work can not exceed totalWork.
		if (totalWork != UNKNOW) {
			if (worked > totalWork) {
				worked = totalWork;
			}
		}

		// Log
		if (isLoggable(lastWriteTimestamp)) {
			float cp = getMainMonitor().getProgress() * 100f / getMainMonitor().getTotalWork();

			lastWriteTimestamp = System.currentTimeMillis();
			getLogger().info("[{}] {}: progress {}%", new Object[] { getBatch().getGroup(), getBatch().getName(), cp });

			if (cp > 100) {
				throw new RuntimeException();
			}
		}

		// Refresh lastUpdate
		touch();
	}

}
