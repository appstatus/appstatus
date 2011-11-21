package net.sf.appstatus.core.batch;

import java.util.List;

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
     * 
     * @param name
     * @param group
     * @param uuid
     * @return
     */
    IBatch addBatch(String name, String group, String uuid);

    /**
     * 
     * @return
     */
    List<IBatch> getErrorBatches();

    List<IBatch> getFinishedBatches();

    IBatchProgressMonitor getMonitor(IBatch batch);

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
}
