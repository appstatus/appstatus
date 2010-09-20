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

import net.sf.appstatus.agent.batch.IJobProgressAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log job progress agent.
 * 
 * @author Guillaume Mary
 * 
 */
public class LogJobProgressAgent implements IJobProgressAgent {

	private static final long DEFAULT_WRITING_DELAY = 5000;

	private static Logger logger = LoggerFactory
			.getLogger(LogJobProgressAgent.class);

	private final String executionId;

	private long lastItemWriteTimestamp;

	private long lastMessageWriteTimestamp;

	private long lastWorkedWriteTimestamp;

	private String name;

	private IJobProgressAgent parent;

	private int parentWork;

	private int rejected;

	private long startTimestamp;

	private int totalWork;

	private int worked;

	private long writingDelay = DEFAULT_WRITING_DELAY;

	/**
	 * Default constructor.
	 * 
	 * @param executionId
	 *            job execution id
	 */
	public LogJobProgressAgent(String executionId) {
		this.executionId = executionId;
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
	private LogJobProgressAgent(String executionId,
			IJobProgressAgent parent, int parentWork) {
		this.executionId = executionId;
		this.parent = parent;
		this.parentWork = parentWork;
	}

	/**
	 * {@inheritDoc}
	 */
	public void beginTask(String name, String group, String description,
			int totalWork) {
		this.name = name;
		this.totalWork = totalWork;
		this.startTimestamp = System.currentTimeMillis();
		logger.info(
				"Start task <{}>-<{}> ({}), id : {} timstamp : {}, totalWork : {}",
				new Object[] { group, name, description, executionId,
						startTimestamp, String.valueOf(totalWork) });
	}

	/**
	 * {@inheritDoc}
	 */
	public IJobProgressAgent createSubTask(int work) {
		return new LogJobProgressAgent(executionId, this, work);
	}

	/**
	 * {@inheritDoc}
	 */
	public void done() {
		if (parent != null) {
			parent.worked(parentWork);
		}
		logger.info(
				"End task <{}> [totalTime : {} ms, total items : {}, processed : {}, rejected : {}]",
				new Object[] { name,
						System.currentTimeMillis() - startTimestamp, totalWork,
						worked, rejected });
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTotalWork() {
		return this.totalWork;
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

	/**
	 * {@inheritDoc}
	 */
	public void message(String message) {
		if (isLoggable(lastMessageWriteTimestamp)) {
			lastMessageWriteTimestamp = System.currentTimeMillis();
			logger.info("Task <{}> message : {}", name, message);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void reject(Object item, String reason, String idMethodName) {
		rejected++;
		logger.info("Task <{}> rejected item : {}. reason : {}", new Object[] {
				name, item, reason });
	}

	/**
	 * {@inheritDoc}
	 */
	public void reject(Object[] items, String reason, String idMethodName) {
		rejected = rejected + items.length;
		logger.info("Task <{}> rejected items : {}. reason : {}", new Object[] {
				name, items, reason });
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCurrentItem(Object item) {
		if (isLoggable(lastItemWriteTimestamp)) {
			lastItemWriteTimestamp = System.currentTimeMillis();
			logger.info("Task <{}> current item : {}", name, item);
		}

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
			logger.info("Task <{}> worked on {} items", name, worked);
		}
	}

}
