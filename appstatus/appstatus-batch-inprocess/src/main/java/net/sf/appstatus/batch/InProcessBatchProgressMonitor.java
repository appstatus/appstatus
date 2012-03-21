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

import net.sf.appstatus.core.batch.AbstractBatchProgressMonitor;
import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;
import net.sf.appstatus.core.batch.IBatchProgressMonitorExt;

import java.util.Date;

/**
 * Log job progress agent.
 * 
 * @author Guillaume Mary
 * @author Nicolas Richeton
 * 
 */
public class InProcessBatchProgressMonitor extends AbstractBatchProgressMonitor
		implements IBatchProgressMonitorExt {

	private InProcessBatchManager manager;

	/**
	 * Default constructor.
	 * 
	 * @param executionId
	 *            job execution id
     * @param manager batch manager
     * @param batch current batch
	 */
	public InProcessBatchProgressMonitor(String executionId, IBatch batch,
			InProcessBatchManager manager) {
		super(executionId, batch);
		this.manager = manager;
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
     * @param batch current batch
	 */
	private InProcessBatchProgressMonitor(String executionId,
			InProcessBatchProgressMonitor parent, int parentWork, Batch batch) {
		super(executionId, parent, parentWork, batch);
        this.manager = parent.manager;
	}

	@Override
	public Batch getBatch() {
		return (Batch) super.getBatch();
	}

	protected InProcessBatchProgressMonitor getCurrentChild() {
		return (InProcessBatchProgressMonitor) currentChild;
	}

	@Override
	public Date getEndDate() {
		return super.getEndDate();
	}

	@Override
	public Date getStartDate() {
		return super.getStartDate();
	}

	@Override
	protected IBatchProgressMonitor newInstance(int work) {
		return new InProcessBatchProgressMonitor(executionId, this, work,
				getBatch());
	}

	@Override
	protected void onBatchEnd() {
		manager.batchEnd(getBatch());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void worked(int work) {
		super.worked(work);

	}

}
