package net.sf.appstatus.samples.service;

import net.sf.appstatus.agent.IServiceMonitorAgent;

public class ServiceSampleCall {

	IServiceMonitorAgent monitor;

	ServiceSample service;

	public void callAService() {
		String id = monitor.beginCall();
		// call the service
		service.myService();
		monitor.endCall(id);
	}
}
