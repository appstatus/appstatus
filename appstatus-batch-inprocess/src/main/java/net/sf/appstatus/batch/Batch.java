package net.sf.appstatus.batch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.appstatus.core.batch.IBatch;

public class Batch implements IBatch {
	String uuid;
	String name;
	String group;
	Date endDate;
	Date startDate = new Date();
	InProcessBatchProgressMonitor monitor;

	public void setMonitor(InProcessBatchProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getEndDate() {
		return monitor.getEstimateEndDate();
	}

	public String getLastMessage() {
		return monitor.getLastMessage();
	}

	public String getCurrentTask(){
		return monitor.getTaskName();
	}
	public float getProgressStatus() {
		if (monitor == null || monitor.getTotalWork() <= 0)
			return -1;

		return  monitor.getProgress() * 100f
				/ (float) monitor.getTotalWork();
	}

	public List<String> getRejectedItemsId() {
	
		return new ArrayList<String>();
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getStatus() {
		return monitor.isDone()?"Completed":"Running";
	}

	public String getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}

	public void setGroup(String group2) {
		this.group = group2;
		
	}

	public void setName(String name2) {
	this.name = name2;
	}

	public String getCurrentItem() {
		return monitor.getCurrentItem().toString();
	}
 
}
