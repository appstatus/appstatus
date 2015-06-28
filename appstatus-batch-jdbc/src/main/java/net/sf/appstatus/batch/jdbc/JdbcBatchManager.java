package net.sf.appstatus.batch.jdbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchManager;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcBatchManager implements IBatchManager {

	private static Logger logger = LoggerFactory.getLogger(JdbcBatchManager.class);

	private BatchDao batchDao;
	List<IBatch> runningBatches = new Vector<IBatch>();
	private int logInterval;

	public void setBatchDao(BatchDao batchDao) {
		this.batchDao = batchDao;
	}

	public void batchEnd(Batch batch) {
		runningBatches.remove(batch);
	}

	public IBatch addBatch(String name, String group, String uuid) {

		BdBatch bdBatch = new BdBatch();
		bdBatch.setName(name);
		bdBatch.setGroup(group);
		bdBatch.setUuid(uuid);
		bdBatch.setStartDate(new Date());
		bdBatch.setStatus(Batch.STATUS_RUNNING);

		IBatch b = new Batch(bdBatch);
		int currentPosition = runningBatches.indexOf(b);
		if (currentPosition >= 0) {
			// Reuse existing object (and keep monitor).
			b = runningBatches.get(currentPosition);
		} else {
			// Add new batch

			// runningBatches is not limited in size.
			runningBatches.add(b);

			batchDao.save(bdBatch);
		}

		return b;
	}

	private List<IBatch> convertToIBatch(List<BdBatch> bdBaches) {
		List<IBatch> result = null;
		if (bdBaches != null) {
			result = new ArrayList<IBatch>();
			for (BdBatch b : bdBaches) {
				result.add(new Batch(b));
			}
		}
		return result;
	}

	public List<IBatch> getErrorBatches() {
		return convertToIBatch(batchDao.fetchError(25));
	}

	public List<IBatch> getFinishedBatches() {
		return convertToIBatch(batchDao.fetchFinished(25));
	}

	public IBatchProgressMonitor getMonitor(IBatch batch) {
		JdbcBatchProgressMonitor monitor = new JdbcBatchProgressMonitor(batch.getUuid(), batch, batchDao);
		monitor.setManager(this);
		return monitor;
	}

	public List<IBatch> getRunningBatches() {
		return convertToIBatch(batchDao.fetchRunning(25));

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.sf.appstatus.core.batch.IBatchManager#removeAllBatches(int)
	 */
	public void removeAllBatches(int scope) {
		switch (scope) {
		case IBatchManager.REMOVE_OLD:
			batchDao.deleteOldBatches(6);
			break;

		default:
			batchDao.deleteSuccessBatches();
			break;
		}
	}

	public void removeBatch(String uuid) {
		batchDao.deleteBatch(uuid);
	}

	public void setConfiguration(Properties configuration) {
		logInterval = Integer.getInteger(configuration.getProperty("batch.logInterval"), 1000);
		logger.info("Batch log interval: {}ms", logInterval);

	}

	public Properties getConfiguration() {
		return null;
	}

	public void init() {
		// Check database for table
		batchDao.createDbIfNecessary();
	}

}
