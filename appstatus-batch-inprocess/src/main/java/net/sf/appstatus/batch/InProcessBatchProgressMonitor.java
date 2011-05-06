package net.sf.appstatus.batch;

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

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;
import net.sf.appstatus.core.batch.IBatchProgressMonitorExt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log job progress agent.
 * 
 * @author Guillaume Mary
 * @author Nicolas Richeton
 * 
 */
public class InProcessBatchProgressMonitor implements IBatchProgressMonitorExt {

	private Batch batch;

	private static Logger logger = LoggerFactory
			.getLogger(InProcessBatchProgressMonitor.class);

	private final String executionId;

	private String lastMessage;

	private String taskName;

	public String getTaskName() {
		return taskName;
	}

	boolean done = false;

	private InProcessBatchProgressMonitor parent;

	private InProcessBatchProgressMonitor currentChild;
	private int currentChildWork = 0;

	private int parentWork;

	private int rejected;

	private int totalWork;

	private int worked = 0;

	float speed = 0;
	long startDate;

	private Object currentItem;

	/**
	 * Default constructor.
	 * 
	 * @param executionId
	 *            job execution id
	 */
	public InProcessBatchProgressMonitor(String executionId, IBatch batch) {
		this.executionId = executionId;
		this.batch = (Batch) batch;
		this.batch.setMonitor(this);
		startDate = System.currentTimeMillis();
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
	private InProcessBatchProgressMonitor(String executionId,
			InProcessBatchProgressMonitor parent, int parentWork, Batch batch) {
		this.executionId = executionId;
		this.parent = parent;
		this.parentWork = parentWork;
		this.batch = batch;
	}

	/**
	 * {@inheritDoc}
	 */
	public void beginTask(String name, String group, String description,
			int totalWork) {
		this.taskName = name;
		this.totalWork = totalWork;
	}

	/**
	 * {@inheritDoc}
	 */
	public IBatchProgressMonitor createSubTask(int work) {
		currentChild = new InProcessBatchProgressMonitor(executionId, this,
				work, batch);
		currentChildWork = work;
		return currentChild;
	}

	/**
	 * {@inheritDoc}
	 */
	public void done() {
		if (parent != null) {
			parent.worked(parentWork);
			parent.currentChild = null;
			parent.currentChildWork = -1;
		}
		done = true;
		currentItem = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTotalWork() {
		return this.totalWork;
	}

	/**
	 * {@inheritDoc}
	 */
	public void message(String message) {
		lastMessage = message;
		if( parent != null ){
			parent.lastMessage = message;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void reject(Object item, String reason, String getIdMethod) {
		rejected++;
	}

	/**
	 * {@inheritDoc}
	 */
	public void reject(Object[] items, String reason, String getIdMethod) {
		rejected = rejected + items.length;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCurrentItem(Object item) {
		currentItem = item;
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

		long seconds = (System.currentTimeMillis() - startDate) / 1000;
		speed = (float) worked / (float) seconds;
	}

	public boolean isCancelRequested() {
		return false;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public float getProgress() {
		if (currentChild != null && currentChild.getTotalWork() > 0) {

			float childProgress = (float) currentChildWork
					* (float) currentChild.getProgress()
					/ (float) currentChild.getTotalWork();

			return (float) worked + childProgress;
		}

		return worked;
	}

	public Date getEstimateEndDate() {
		long currentTime = System.currentTimeMillis();

		long elapsed = currentTime - startDate;

		long endTime = currentTime
				+ (long) (totalWork * elapsed / getProgress());

		return new Date(endTime);
	}

	public boolean isDone() {
		return done;
	}

	public Object getCurrentItem() {
		return currentItem;
	}
}
