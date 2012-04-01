package net.sf.appstatus.batch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

/**
 * Bean batch implementation, it used a progress monitor to track the batch
 * status informations.
 * 
 */
public class Batch implements IBatch {

	private final String group;
	private InProcessBatchProgressMonitor monitor;
	private final String name;

	private final String uuid;

	/**
	 * Creates a new Batch.
	 * <p>
	 * This method is not intended to be used directly.
	 * 
	 * @param uuid
	 *            unique batch identifier
	 */
	public Batch(String uuid) {
		this(uuid, null, null);
	}

	/**
	 * Creates a new Batch.
	 * <p>
	 * This method is not intended to be used directly.
	 * 
	 * @param uuid
	 *            unique batch identifier
	 * @param name
	 *            batch name
	 * @param group
	 *            batch group name
	 */
	public Batch(String uuid, String name, String group) {
		if (uuid == null) {
			throw new IllegalArgumentException("Batch uuid cannot be null");
		}
		this.uuid = uuid;
		this.name = name;
		this.group = group;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Batch)) {
			return false;
		}

		return uuid.equals(((Batch) obj).getUuid());
	}

	/**
	 * Retrieve the current processed item.
	 */
	public String getCurrentItem() {
		if (monitor == null) {
			return null;
		}

		Object currentItem = monitor.getCurrentItem();
		if (currentItem == null) {
			return null;
		}
		return currentItem.toString();
	}

	/**
	 * Retrieve the current task.
	 */
	public String getCurrentTask() {
		if (monitor == null) {
			return null;
		}
		return monitor.getTaskName();
	}

	public Date getEndDate() {
		if (monitor == null) {
			return null;
		}
		return monitor.getEndDate();
	}

	public String getGroup() {
		return group;
	}

	public long getItemCount() {
		if (monitor == null) {
			return 0;
		}
		return monitor.getItemCount();
	}

	public String getLastMessage() {
		if (monitor == null) {
			return null;
		}
		return monitor.getLastMessage();
	}

	public Date getLastUpdate() {
		if (monitor == null) {
			return null;
		}
		return monitor.getLastUpdate();
	}

	public String getName() {
		return name;
	}

	public IBatchProgressMonitor getProgressMonitor() {
		return monitor;
	}

	public float getProgressStatus() {
		if (monitor == null || monitor.getTotalWork() == IBatchProgressMonitor.UNKNOW || monitor.getTotalWork() == 0) {
			return IBatchProgressMonitor.UNKNOW;
		}

		return monitor.getProgress() * 100f / monitor.getTotalWork();
	}

	public List<String> getRejectedItemsId() {
		if (monitor == null) {
			return new ArrayList<String>();
		}
		return monitor.getRejectedItems();
	}

	public Date getStartDate() {
		if (monitor == null) {
			return null;
		}
		return monitor.getStartDate();
	}

	public String getStatus() {
		if (monitor == null) {
			return null;
		}

		if (!monitor.isDone()) {
			return STATUS_RUNNING;
		}

		if (monitor.isSuccess()) {
			return STATUS_SUCCESS;
		}

		return STATUS_FAILURE;
	}

	public String getUuid() {
		return uuid;
	}

	public boolean isSuccess() {
		if (monitor == null) {
			return false;
		}
		return monitor.isSuccess();
	}

	/**
	 * @inheritDoc
	 * 
	 * @see net.sf.appstatus.core.batch.IBatch#setProgressMonitor(net.sf.appstatus.core.batch.IBatchProgressMonitor)
	 */
	public void setProgressMonitor(IBatchProgressMonitor monitor) {
		this.monitor = (InProcessBatchProgressMonitor) monitor;
	}

}
