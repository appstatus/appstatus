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

	private static final long DEFAULT_WRITING_DELAY = 5000;
	private static Logger logger = LoggerFactory
			.getLogger(AbstractBatchProgressMonitor.class);

	/**
	 * Reference to the batch description
	 */
	private IBatch batch;
	protected IBatchProgressMonitor currentChild;

	protected int currentChildWork = 0;

	/**
	 * Current item being processed
	 */
	protected Object currentItem;

	protected boolean done = false;
	protected final String executionId;
	private long lastItemWriteTimestamp;

	private String lastMessage;
	private long lastMessageWriteTimestamp;

	private long lastWorkedWriteTimestamp;

	private String name;

	protected AbstractBatchProgressMonitor parent;

	private int parentWork;

	private int rejected = 0;

	protected long startTime;

	private boolean success;

	private String taskDescription;

	private String taskGroup;

	protected String taskName;

	protected int totalWork = UNKNOW;

	protected int worked = 0;

	private long writingDelay = DEFAULT_WRITING_DELAY;

	/**
	 * Default constructor.
	 * 
	 * @param executionId
	 *            job execution id
	 */
	public AbstractBatchProgressMonitor(String executionId, IBatch batch) {
		this.executionId = executionId;
		this.batch = batch;
		this.batch.setProgressMonitor(this);
		startTime = System.currentTimeMillis();
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
				"Start task <{}> ({}), id : {}, totalWork : {}",
				new Object[] { name, description, executionId,
						String.valueOf(totalWork) });
	}

	/**
	 * {@inheritDoc}
	 */
	public IBatchProgressMonitor createSubTask(int work) {
		currentChild = newInstance(work);
		currentChildWork = work;
		return currentChild;
	}

	/**
	 * {@inheritDoc}
	 */
	public void done() {
		endBatch();

		success = true;
		getLogger()
				.info("End task <{}> [totalTime : {} ms, total items : {}, processed : {}, rejected : {}]",
						new Object[] { name,
								System.currentTimeMillis() - startTime,
								totalWork, worked, rejected });
	}

	protected void endBatch() {
		if (parent != null) {
			parent.worked(parentWork);
			parent.currentChild = null;
			parent.currentChildWork = -1;
		}

		currentItem = null;
		done = true;
	}

	public void fail(String reason) {
		getLogger().error("Task has success  <{}>-<{}>, id : {}, reason : {}",
				new Object[] { taskGroup, taskName, executionId, reason });

		message("Failed: " + reason);

		// Mark job as finished
		endBatch();

		this.success = false;

	}

	public IBatch getBatch() {
		return batch;
	}

	public Object getCurrentItem() {
		return currentItem;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	protected Logger getLogger() {
		return logger;
	}

	public float getProgress() {
		return worked;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public String getTaskGroup() {
		return taskGroup;
	}

	public String getTaskName() {
		return taskName;
	};

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
		if (isLoggable(lastMessageWriteTimestamp)) {
			lastMessageWriteTimestamp = System.currentTimeMillis();
			getLogger().info("Task <{}> message : {}", name, message);
		}
		lastMessage = message;
		if (parent != null) {
			parent.lastMessage = message;
		}
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
		rejected++;
		getLogger().warn("Task <{}> rejected item : {}. reason : {}",
				new Object[] { name, itemId, reason }, e);

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
		rejected = rejected + itemIds.length;
		getLogger().warn("Task <{}> rejected items : {}. reason : {}",
				new Object[] { name, itemIds, reason }, e);

	}

	/**
	 * {@inheritDoc}
	 */
	public void setCurrentItem(Object item) {
		currentItem = item;

		if (isLoggable(lastItemWriteTimestamp)) {
			lastItemWriteTimestamp = System.currentTimeMillis();
			getLogger().info("Task <{}> current item : {}", name, item);
		}

	}

	public void setLogger(Logger logger) {
		this.logger = logger;
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

	/**
	 * {@inheritDoc}
	 */
	public void worked(int work) {
		worked = worked + work;

		if (isLoggable(lastWorkedWriteTimestamp)) {
			lastWorkedWriteTimestamp = System.currentTimeMillis();
			getLogger().info("Task <{}> worked on {} items", name, worked);
		}
	}

}
