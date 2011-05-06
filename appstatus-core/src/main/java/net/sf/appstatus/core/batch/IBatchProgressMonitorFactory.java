package net.sf.appstatus.core.batch;

public interface IBatchProgressMonitorFactory {
	IBatchProgressMonitor getMonitor(String executionId);
}
