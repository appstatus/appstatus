package net.sf.appstatus.batch;

import java.util.Date;
import java.util.List;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

public class Batch implements IBatch {
    Date endDate;
    String group;
    InProcessBatchProgressMonitor monitor;
    String name;
    String uuid;

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

    public String getCurrentItem() {
        return monitor.getCurrentItem().toString();
    }

    public String getCurrentTask() {
        return monitor.getTaskName();
    }

    public Date getEndDate() {
        return monitor.getEndDate();
    }

    public String getGroup() {
        return group;
    }

    public long getItemCount() {
        return monitor.getItemCount();
    }

    public String getLastMessage() {
        return monitor.getLastMessage();
    }

    public Date getLastUpdate() {
        return monitor.getLastUpdate();
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
        return monitor.getRejectedItems();
    }

    public Date getStartDate() {
        return monitor.getStartDate();
    }

    public String getStatus() {

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
        return monitor.isSuccess();
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
