package net.sf.appstatus.batch;

import java.util.List;
import java.util.Vector;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchManager;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

public class InProcessBatchManager implements IBatchManager {
	private long maxSize = 25;

	List<IBatch> runningBatches = new Vector<IBatch>();

	public IBatch addBatch(String name, String group, String uuid) {

		// Ensure batch list does not exceed defined size
		if (runningBatches.size() >= maxSize) {
			runningBatches.remove(0);
		}

		// Add batch
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

	public IBatchProgressMonitor getMonitor(IBatch batch) {
		return new InProcessBatchProgressMonitor(batch.getUuid(), batch);
	}

	public List<IBatch> getRunningBatches() {
		return runningBatches;
	}

	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}

}
