package net.sf.appstatus.core.batch;

import java.util.List;

public interface IBatchStorage {

	IBatch addBatch(String name, String group, String uuid);

	List<IBatch> getFinishedBatches();

	List<IBatch> getRunningBatches();

}
