package net.sf.appstatus.agent.service.impl;

import net.sf.appstatus.agent.service.IServiceMonitorAgent;
import net.sf.appstatus.agent.service.IServiceMonitorAgentFactory;

public class LogServiceMonitorAgentFactory implements
		IServiceMonitorAgentFactory {

	public IServiceMonitorAgent getAgent() {
		return new LogServiceMonitorAgent();
	}

}
