package net.sf.appstatus.core.batch;

import java.util.List;

public interface IBatchManager {

	IBatch addBatch(String name, String group, String uuid);

	List<IBatch> getFinishedBatches();

	IBatchProgressMonitor getMonitor(IBatch batch);

	List<IBatch> getRunningBatches();

}
