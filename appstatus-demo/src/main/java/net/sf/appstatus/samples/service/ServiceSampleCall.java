package net.sf.appstatus.samples.service;

import net.sf.appstatus.agent.service.IServiceMonitorAgent;
import net.sf.appstatus.agent.service.ServiceMonitorAgentFactory;

public class ServiceSampleCall {

	private static IServiceMonitorAgent monitor = ServiceMonitorAgentFactory
			.getAgent();

	ServiceSample service;

	public void callAService() {

		String id = monitor.beginCall("myService", null);
		// call the service
		service.myService();
		monitor.endCall(id);
	}
}
