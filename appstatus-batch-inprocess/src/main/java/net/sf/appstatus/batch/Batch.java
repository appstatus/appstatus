package net.sf.appstatus.batch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

public class Batch implements IBatch {
	Date endDate;
	String group;
	InProcessBatchProgressMonitor monitor;
	String name;
	Date startDate = new Date();
	String uuid;

	public String getCurrentItem() {
		return monitor.getCurrentItem().toString();
	}

	public String getCurrentTask() {
		return monitor.getTaskName();
	}

	public Date getEndDate() {
		return monitor.getEstimateEndDate();
	}

	public String getGroup() {
		return group;
	}

	public String getLastMessage() {
		return monitor.getLastMessage();
	}

	public String getName() {
		return name;
	}

	public IBatchProgressMonitor getProgressMonitor() {
		return monitor;
	}

	public float getProgressStatus() {
		if (monitor == null || monitor.getTotalWork() <= 0) {
			return -1;
		}

		return monitor.getProgress() * 100f / monitor.getTotalWork();
	}

	public List<String> getRejectedItemsId() {

		return new ArrayList<String>();
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getStatus() {
		return monitor.isDone() ? "Completed" : "Running";
	}

	public String getUuid() {
		return uuid;
	}

	public void setGroup(String group2) {
		this.group = group2;

	}

	public void setName(String name2) {
		this.name = name2;
	}

	public void setProgressMonitor(IBatchProgressMonitor monitor) {
		this.monitor = (InProcessBatchProgressMonitor) monitor;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
