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

import net.sf.appstatus.core.batch.AbstractBatchProgressMonitor;
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
public class InProcessBatchProgressMonitor extends AbstractBatchProgressMonitor
		implements IBatchProgressMonitorExt {

	private static Logger logger = LoggerFactory
			.getLogger(InProcessBatchProgressMonitor.class);

	/**
	 * Default constructor.
	 * 
	 * @param executionId
	 *            job execution id
	 */
	public InProcessBatchProgressMonitor(String executionId, IBatch batch) {
		super(executionId, batch);
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
		super(executionId, parent, parentWork, batch);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void worked(int work) {
		super.worked(work);

	}

}
