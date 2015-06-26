package net.sf.appstatus.batch.jdbc;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Log job progress agent.
 * 
 * @author Nicolas Richeton
 * 
 */
public class JdbcBatchProgressMonitor extends AbstractBatchProgressMonitor
		implements IBatchProgressMonitorExt {
	private static Logger logger = LoggerFactory
			.getLogger(JdbcBatchProgressMonitor.class);
	BatchDao batchDao;

	private long lastDbSave;
	private long writingDelay = 1000;

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
	private JdbcBatchProgressMonitor(String executionId,
			JdbcBatchProgressMonitor parent, int parentWork, Batch batch,
			BatchDao bachDao) {
		super(executionId, parent, parentWork, batch);
		this.batchDao = bachDao;
	}

	/**
	 * Default constructor.
	 * 
	 * @param executionId
	 *            job execution id
	 */
	public JdbcBatchProgressMonitor(String executionId, IBatch batch,
			BatchDao bachDao) {
		super(executionId, batch);
		this.batchDao = bachDao;

	}

	@Override
	public void done() {
		super.done();
		updateDb(true);
	}

	@Override
	public void fail(String reason) {

		super.fail(reason);

		updateDb(true);
	}

	@Override
	public Batch getBatch() {
		return (Batch) super.getBatch();
	}


	@Override
	protected JdbcBatchProgressMonitor getMainMonitor() {
		return (JdbcBatchProgressMonitor) super.getMainMonitor();
	}


	private boolean isLoggable(long lastWriteTimestamp) {
		if (System.currentTimeMillis() - lastWriteTimestamp > writingDelay) {
			return true;
		}
		return false;
	}

	@Override
	public void message(String message) {
		super.message(message);

		updateDb(true);

	}

	@Override
	protected IBatchProgressMonitor newInstance(int work) {
		return new JdbcBatchProgressMonitor(executionId, this, work,
				getBatch(), batchDao);
	}

	private String readableStatus() {
		if (!getMainMonitor().isDone()) {
			return IBatch.STATUS_RUNNING;
		}

		if (getMainMonitor().isSuccess()) {
			return IBatch.STATUS_SUCCESS;
		}

		return IBatch.STATUS_FAILURE;
	}

	@Override
	public void reject(String itemId, String reason, Throwable e) {
		super.reject(itemId, reason, e);
		updateDb(true);
	}

	@Override
	public void setCurrentItem(Object item) {
		super.setCurrentItem(item);

		updateDb(false);
	}

	private void updateDb(boolean force) {
		if (force || isLoggable(lastDbSave)) {
			lastDbSave = System.currentTimeMillis();
			getBatch().getBdBatch().setStatus(readableStatus());
			getBatch().getBdBatch().setCurrentItem((String) currentItem);

			if (!org.apache.commons.lang3.StringUtils.isEmpty(getMainMonitor()
					.getLastMessage())
					&& getMainMonitor().getLastMessage().length() > 1024) {
				getBatch().getBdBatch().setLastMessage(
						getMainMonitor().getLastMessage().substring(0, 1023));
			} else {
				getBatch().getBdBatch().setLastMessage(
						getMainMonitor().getLastMessage());
			}
			getBatch().getBdBatch().setStartDate(
					getMainMonitor().getStartDate());
			getBatch().getBdBatch().setEndDate(getMainMonitor().getEndDate());
			getBatch().getBdBatch().setCurrentTask(taskName);
			getBatch().getBdBatch().setProgress(
					getMainMonitor().getProgress() * 100f
							/ getMainMonitor().getTotalWork());
			getBatch().getBdBatch().setLastUpdate(
					getMainMonitor().getLastUpdate());
			getBatch().getBdBatch().setSuccess(getMainMonitor().isSuccess());
			getBatch().getBdBatch().setReject(
					StringUtils.collectionToDelimitedString(getMainMonitor()
							.getRejectedItems(), "|"));
			getBatch().getBdBatch().setItemCount(
					getMainMonitor().getItemCount());
			batchDao.update(getBatch().getBdBatch());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void worked(int work) {
		super.worked(work);

		updateDb(false);

	}

}
