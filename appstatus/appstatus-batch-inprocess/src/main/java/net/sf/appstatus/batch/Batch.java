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
	private static final String NONE = "none";

	private static final String UNKNOWN = "unknown";
	private final String group;
	private InProcessBatchProgressMonitor monitor;
	private final String name;

	private final String uuid;

	/**
	 * Default contructor.
	 * 
	 * @param uuid
	 *            unique batch identifier
	 */
	public Batch(String uuid) {
		this.uuid = uuid;
		this.group = UNKNOWN;
		this.name = UNKNOWN;
	}

	/**
	 * Constructor with a name and a group.
	 * 
	 * @param uuid
	 *            unique batch identifier
	 * @param name
	 *            batch name
	 * @param group
	 *            batch group name
	 */
	public Batch(String uuid, String name, String group) {
		this.uuid = uuid;
		this.name = name;
		this.group = group;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Batch)) {
			return false;
		}

		Batch objBatch = (Batch) obj;

		return this.getUuid().equals(objBatch.getUuid());
	}

	/**
	 * Retrieve the current processed item.
	 */
	public String getCurrentItem() {
		if (monitor == null) {
			return NONE;
		}
		final Object currentItem = monitor.getCurrentItem();
		if (currentItem == null) {
			return NONE;
		}
		return currentItem.toString();
	}

	/**
	 * Retrieve the current task.
	 */
	public String getCurrentTask() {
		if (monitor == null) {
			return NONE;
		}
		final String taskName = monitor.getTaskName();
		if (taskName == null) {
			return NONE;
		}
		return taskName;
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
			return NONE;
		}
		final String lastMessage = monitor.getLastMessage();
		if (lastMessage == null) {
			return NONE;
		}
		return lastMessage;
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
		if (monitor == null || monitor.getTotalWork() <= 0
				|| monitor.getProgress() < 0) {
			return -1;
		}

		return monitor.getProgress() * 100f / monitor.getTotalWork();
	}

	public List<String> getRejectedItemsId() {
		if (monitor == null) {
			return new ArrayList<String>();
		}
		final List<String> rejectedItems = monitor.getRejectedItems();
		if (rejectedItems == null) {
			return new ArrayList<String>();
		}
		return rejectedItems;
	}

	public Date getStartDate() {
		if (monitor == null) {
			return null;
		}
		return monitor.getStartDate();
	}

	public String getStatus() {
		if (monitor == null) {
			return UNKNOWN;
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

	public void setProgressMonitor(IBatchProgressMonitor monitor) {
		this.monitor = (InProcessBatchProgressMonitor) monitor;
	}

}
