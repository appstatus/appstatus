package net.sf.appstatus.agent.batch.impl;

import static org.mockito.Mockito.mock;

import java.util.Date;
import java.util.List;

import net.sf.appstatus.agent.batch.IBatchExecutionMonitor;
import net.sf.appstatus.agent.batch.IBatchExecutionMonitorAgent;

public class MockBatchMonitorAgent implements IBatchExecutionMonitorAgent, IBatchExecutionMonitor {

	private final IBatchExecutionMonitorAgent mockAgent = mock(IBatchExecutionMonitorAgent.class);

	private final IBatchExecutionMonitor mockMonitor = mock(IBatchExecutionMonitor.class);

	private final String batchName;

	public MockBatchMonitorAgent(String batchName) {
		this.batchName = batchName;
	}

	public void beginTask(String name, String group, String description,
			int totalWork) {
		mockAgent.beginTask(name, group, description, totalWork);
	}

	public IBatchExecutionMonitorAgent createSubTask(int work) {
		return mockAgent.createSubTask(work);
	}

	public void done() {
		mockAgent.done();
	}

	public Date getEndDate(String executionId) {
		return mockMonitor.getEndDate(executionId);
	}

	public List<String> getJobExecutionIds() {
		return mockMonitor.getJobExecutionIds();
	}

	public List<String> getLastMessages(String executionId, int nbMessage) {
		return mockMonitor.getLastMessages(executionId, nbMessage);
	}

	public double getProgressStatus(String executionId) {
		return mockMonitor.getProgressStatus(executionId);
	}

	public List<String> getRejectedItemsId(String executionId) {
		return mockMonitor.getRejectedItemsId(executionId);
	}

	public Date getStartDate(String executionId) {
		return mockMonitor.getStartDate(executionId);
	}

	public String getStatus(String executionId) {
		return mockMonitor.getStatus(executionId);
	}

	public int getTotalWork() {
		return mockAgent.getTotalWork();
	}

	public void message(String message) {
		mockAgent.message(message);
	}

	public void reject(Object item, String reason, String idMethodName) {
		mockAgent.reject(item, reason, idMethodName);
	}

	public void reject(Object[] items, String reason, String idMethodName) {
		mockAgent.reject(items, reason, idMethodName);
	}

	public void setCurrentItem(Object item) {
		mockAgent.setCurrentItem(item);
	}

	public void setTotalWork(int totalWork) {
		mockAgent.setTotalWork(totalWork);
	}

	public void worked(int work) {
		mockAgent.worked(work);
	}

}
