package net.sf.appstatus.core.batch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * Abstract implementation of IBatchExprAdapter.
 *
 * @author Idriss Neumann
 *
 */
public abstract class AbstractBatchExprAdapter implements IBatchExprAdapter {
    protected List<BatchConfiguration> batchConfigurations;

    /**
     * Constructor.
     */
    public AbstractBatchExprAdapter() {
        batchConfigurations = new ArrayList<BatchConfiguration>();
    }

    /**
     * Getting the batch configuration for a Single batch.
     *
     * @param group
     * @param name
     * @return BatchConfiguration
     */
    public BatchConfiguration getBatchConfiguration(String group, String name) {
        if (CollectionUtils.isNotEmpty(batchConfigurations)) {
            for (BatchConfiguration conf : batchConfigurations) {
                if (group.equals(conf.getGroup()) && name.equals(conf.getName())) {
                    return conf;
                }
            }
        }
        return null;
    }

    /**
     * Getting the batch's configurations.
     */
    public List<BatchConfiguration> getBatchConfigurations() {
        return this.batchConfigurations;
    }
}
