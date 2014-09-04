package net.sf.appstatus.batch.jdbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchManager;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

public class JdbcBatchManager implements IBatchManager {

    private BatchDao batchDao;

    public void setBatchDao(BatchDao batchDao) {
        this.batchDao = batchDao;
    }

    public IBatch addBatch(String name, String group, String uuid) {
        BdBatch b = new BdBatch();
        b.setName(name);
        b.setGroup(group);
        b.setUuid(uuid);
        b.setStartDate(new Date());
        b.setStatus(Batch.STATUS_RUNNING);
        batchDao.save(b);
        return new Batch(b);
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
        return new JdbcBatchProgressMonitor(batch.getUuid(), batch, batchDao);
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
    }

	public Properties getConfiguration() {
		return null;
	}

}
