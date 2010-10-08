package net.sf.appstatus.agent.service.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.sf.appstatus.agent.service.IServiceMonitor;
import net.sf.appstatus.agent.service.IServiceMonitorAgent;

public class MockServiceMonitorAgent implements IServiceMonitorAgent,
		IServiceMonitor {

	private final IServiceMonitorAgent mockAgent = mock(IServiceMonitorAgent.class);

	private final IServiceMonitor mockMonitor = mock(IServiceMonitor.class);

	private final String serviceName;

	public MockServiceMonitorAgent(String serviceName) {
		this.serviceName = serviceName;
	}

	public String beginCall(String operationName, Object[] parameters) {
		when(mockAgent.beginCall(operationName, parameters)).thenReturn(
				"Call :" + serviceName + "." + operationName + "("
						+ parameters.toString() + ")");
		return mockAgent.beginCall(operationName, parameters);
	}

	public void endCall(String operationName, String executionId) {
		mockAgent.endCall(operationName, executionId);
	}

	public float getAverageFlow(String operationName) {
		when(mockMonitor.getAverageFlow(operationName)).thenReturn(
				new Float(3 / 2));
		return mockMonitor.getAverageFlow(operationName);
	}

	public float getAverageResponseTime(String operationName) {
		when(mockMonitor.getAverageResponseTime(operationName)).thenReturn(
				new Float(1 / 2));
		return mockMonitor.getAverageResponseTime(operationName);
	}

	public List<String> getOperationNames() {
		List<String> list = new ArrayList<String>();
		list.add("test");
		list.add("test2");
		when(mockMonitor.getOperationNames()).thenReturn(list);
		return null;
	}
}
