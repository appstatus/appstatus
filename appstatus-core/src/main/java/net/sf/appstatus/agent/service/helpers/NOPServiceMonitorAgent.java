/*
 * Copyright 2010 Capgemini
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 */
package net.sf.appstatus.agent.service.helpers;

import net.sf.appstatus.agent.service.IServiceMonitorAgent;

/**
 * A direct NOP (no operation) implementation of {@link IServiceMonitorAgent}.
 * 
 * @author Guillaume Mary
 * 
 */
public class NOPServiceMonitorAgent implements IServiceMonitorAgent {

	/**
	 * The unique instance of NOPServiceMonitorAgent.
	 */
	public static final NOPServiceMonitorAgent NOP_SERVICE_MONITOR_AGENT = new NOPServiceMonitorAgent();

	/**
	 * There is no point in creating multiple instances of
	 * NOPServiceMonitorAgent, except by derived classes, hence the protected
	 * access for the constructor.
	 */
	protected NOPServiceMonitorAgent() {
	}

	/**
	 * Always return the string value "NOP".
	 */
	public String beginCall(String serviceName, Object[] parameters) {
		return "NOP";
	}

	/**
	 * A NOP implementation
	 */
	public void endCall(String operationName, String executionId) {
		// NOP
	}

}
