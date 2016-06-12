package net.sf.appstatus.batch.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

public final class CleanBatchHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanBatchHelper.class);

    /**
     * Mark all running batches as done.
     *
     * @param appStatus
     * @return the number of running batches that has been "done".
     */
    public static int cleanBatches(AppStatus appStatus) {
        int count = 0;
        LOGGER.debug(String.format("Number of Running batches [%d] before cleaning",
                appStatus.getBatchManager().getRunningBatches().size()));
        for (IBatch batch : appStatus.getBatchManager().getRunningBatches()) {
            IBatchProgressMonitor m = appStatus.getBatchProgressMonitor(batch.getName(), batch.getGroup(),
                    batch.getUuid());
            LOGGER.debug(String.format("Ending batch group[%s] name[%s] uuid[%s] progess[%s]", batch.getGroup(),
                    batch.getName(), batch.getUuid(), batch.getProgressStatus()));
            m.done();
            count++;
        }
        LOGGER.debug(String.format("Number of Running batches [%d] after cleaning",
                appStatus.getBatchManager().getRunningBatches().size()));
        return count;
    }

    private CleanBatchHelper() {
    }

}
