package net.sf.appstatus.batch;

import java.util.List;
import java.util.Vector;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;
import net.sf.appstatus.core.batch.IBatchStorage;

public class InProcessBatchStorage implements IBatchStorage {

	List<IBatch> runningBatches = new Vector<IBatch>();

	public List<IBatch> getRunningBatches() {
		return runningBatches;
	}

	public IBatch addBatch(String name, String group, String uuid) {
		Batch b = new Batch();
		b.setName(name);
		b.setGroup(group);
		b.setUuid(uuid);
		runningBatches.add(b);
		return b;
	}

	public List<IBatch> getFinishedBatches() {
		return runningBatches;
	}

}
