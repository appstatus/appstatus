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

import net.sf.appstatus.agent.service.IServiceAgent;

/**
 * A direct NOP (no operation) implementation of {@link IServiceAgent}.
 * 
 * @author Guillaume Mary
 * 
 */
public class NOPServiceAgent implements IServiceAgent {

	/**
	 * The unique instance of NOPServiceAgent.
	 */
	public static final NOPServiceAgent NOP_SERVICE_AGENT = new NOPServiceAgent();

	/**
	 * There is no point in creating multiple instances of NOPServiceAgent,
	 * except by derived classes, hence the protected access for the
	 * constructor.
	 */
	protected NOPServiceAgent() {
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
