package net.sf.appstatus.batch.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class Batch implements IBatch {
	BdBatch dbBatch = null;
	JdbcBatchProgressMonitor monitor;

	public Batch(BdBatch bdBatch) {
		this.dbBatch = bdBatch;
	}

	public BdBatch getBdBatch() {
		return dbBatch;
	}

	public String getCurrentItem() {
		return dbBatch.getCurrentItem();
	}

	public String getCurrentTask() {
		return dbBatch.getCurrentTask();
	}

	public Date getEndDate() {
		return dbBatch.getEndDate();
	}

	public String getGroup() {
		return dbBatch.getGroup();
	}

	public long getItemCount() {
		return dbBatch.getItemCount();
	}

	public String getLastMessage() {
		return dbBatch.getLastMessage();
	}

	public Date getLastUpdate() {
		return dbBatch.getLastUpdate();
	}

	public String getName() {
		return dbBatch.getName();
	}

	public IBatchProgressMonitor getProgressMonitor() {
		return (IBatchProgressMonitor) monitor;
	}

	public float getProgressStatus() {
		return dbBatch.getProgress();
	}

	public List<String> getRejectedItemsId() {
		if (dbBatch.getReject() == null) {
			return new ArrayList<String>();
		}
		return Arrays.asList(StringUtils.split(dbBatch.getReject(), "|"));
	}

	public Date getStartDate() {
		return dbBatch.getStartDate();
	}

	public String getStatus() {
		return dbBatch.getStatus();
	}

	public String getUuid() {
		return dbBatch.getUuid();
	}

	public boolean isSuccess() {
		return BooleanUtils.isTrue(dbBatch.getSuccess());
	}

	public void setProgressMonitor(IBatchProgressMonitor monitor) {
		this.monitor = (JdbcBatchProgressMonitor) monitor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dbBatch == null) ? 0 : dbBatch.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Batch other = (Batch) obj;
		if (dbBatch == null) {
			if (other.dbBatch != null) {
				return false;
			}
		} else if (!dbBatch.equals(other.dbBatch)) {
			return false;
		}
		return true;
	}

	
}