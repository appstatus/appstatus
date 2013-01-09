package net.sf.appstatus.core.batch;

import java.util.List;
import java.util.Properties;

import net.sf.appstatus.core.AppStatus;

/**
 * 
 * @author Nicolas Richeton
 * 
 */
public interface IBatchManager {

	/**
	 * Removes batches older than 6 months (default), or older than value
	 * specified in configuration.
	 */
	public int REMOVE_OLD = 1;

	/**
	 * Removes all jobs with no error or rejects.
	 */
	public int REMOVE_SUCCESS = 2;

	/**
	 * Creates and adds a new Batch to the batch manager.
	 * <p>
	 * If the batch already exists (same uuid). The existing one is returned.
	 * 
	 * <p>
	 * NOTE: This method is not intended to be called directly.
	 * 
	 * @see AppStatus#getBatchProgressMonitor()
	 * 
	 * @param name
	 * @param group
	 * @param uuid
	 * @return new or existing batch object.
	 * 
	 */
	IBatch addBatch(String name, String group, String uuid);

	/**
	 * 
	 * @return
	 */
	List<IBatch> getErrorBatches();

	List<IBatch> getFinishedBatches();

	/**
	 * Returns the batch monitor for this batch.
	 * <p>
	 * The same progress monitor is always returned. It is expected that only a
	 * single thread updates the monitor.
	 * 
	 * @param batch
	 * @return
	 */
	IBatchProgressMonitor getMonitor(IBatch batch);

	/**
	 * Returns the list of batchs which are currently running.
	 * 
	 * @return
	 */
	List<IBatch> getRunningBatches();

	/**
	 * Removes all jobs matching the scope value.
	 * 
	 * @param scope
	 *            {@value #REMOVE_OLD} or {@link #REMOVE_SUCCESS}
	 */
	void removeAllBatches(int scope);

	/**
	 * Removes a specific job.
	 * 
	 * @param b
	 */
	void removeBatch(String uuid);

	/**
	 * Inject configuration for service manager.
	 * 
	 * @param configuration
	 */
	void setConfiguration(Properties configuration);
}
