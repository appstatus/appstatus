package net.sf.appstatus.batch;

import java.util.List;
import java.util.Vector;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchManager;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

public class InProcessBatchManager implements IBatchManager {
	List<IBatch> errorBatches = new Vector<IBatch>();

	List<IBatch> finishedBatches = new Vector<IBatch>();
	private long maxSize = 25;
	List<IBatch> runningBatches = new Vector<IBatch>();

	public IBatch addBatch(String name, String group, String uuid) {

		// Add batch
		Batch b = new Batch();
		b.setName(name);
		b.setGroup(group);
		b.setUuid(uuid);
		addTo(runningBatches, b);

		return b;
	}

	protected void addTo(List<IBatch> l, IBatch b) {
		// Ensure batch list does not exceed defined size
		if (l.size() >= maxSize) {
			l.remove(0);
		}

		l.add(b);
	}

	public void batchEnd(Batch batch) {

		runningBatches.remove(batch);
		addTo(finishedBatches, batch);

		if (!batch.isSuccess()) {
			addTo(errorBatches, batch);
		}
	}

	public List<IBatch> getErrorBatches() {
		return errorBatches;
	}

	public List<IBatch> getFinishedBatches() {
		return finishedBatches;
	}

	public IBatchProgressMonitor getMonitor(IBatch batch) {
		return new InProcessBatchProgressMonitor(batch.getUuid(), batch, this);
	}

	public List<IBatch> getRunningBatches() {
		return runningBatches;
	}

	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}

}
