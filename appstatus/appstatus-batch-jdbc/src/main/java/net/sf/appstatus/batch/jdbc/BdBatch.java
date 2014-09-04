package net.sf.appstatus.batch.jdbc;

import java.util.Date;

public class BdBatch {

	private String currentItem;

	private String currentTask;

	private Date endDate;

	private String group;

	private long itemCount;

	private String lastMessage;

	private Date lastUpdate;

	private String name;

	private Float progress;

	private String reject;

	private Date startDate;

	private String status;

	private Boolean success;

	private String uuid;

	public String getCurrentItem() {
		return currentItem;
	}

	public String getCurrentTask() {
		return currentTask;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getGroup() {
		return group;
	}

	public long getItemCount() {
		return itemCount;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public String getName() {
		return name;
	}

	public Float getProgress() {
		return progress;
	}

	public String getReject() {
		return reject;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getStatus() {
		return status;
	}

	public Boolean getSuccess() {
		return success;
	}

	public String getUuid() {
		return uuid;
	}

	public void setCurrentItem(String currentItem) {
		this.currentItem = currentItem;
	}

	public void setCurrentTask(String currentTask) {
		this.currentTask = currentTask;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setItemCount(long itemCount) {
		this.itemCount = itemCount;
	}

	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProgress(Float progress) {
		this.progress = progress;
	}

	public void setReject(String reject) {
		this.reject = reject;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public void setUuid(String id) {
		this.uuid = id;
	}
}
