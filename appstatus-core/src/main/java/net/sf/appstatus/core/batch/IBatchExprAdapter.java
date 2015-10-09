package net.sf.appstatus.core.batch;

import java.util.Date;
import java.util.List;

/**
 * Interface which must be implemented to read a set of execution expr.
 *
 * @author Idriss Neumann
 *
 */
public interface IBatchExprAdapter {
    /**
     * Getting the batch configuration for a Single batch
     * 
     * @param group
     * @param name
     * @return BatchConfiguration
     */
    BatchConfiguration getBatchConfiguration(String group, String name);

    /**
     * Getting the batch's configurations.
     */
    List<BatchConfiguration> getBatchConfigurations();

    /**
     * Convert the execution expr (cron for example).
     *
     * @param expr
     * @param lastExecution
     * @return the next date wich correspond to the expr
     */
    Date getNextDate(String expr, Date lastExecution);
}
