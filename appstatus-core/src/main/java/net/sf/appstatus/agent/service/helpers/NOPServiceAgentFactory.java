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
import net.sf.appstatus.agent.service.IServiceAgentFactory;

/**
 * Return the unique instance of the {@link NOPServiceAgent}
 * 
 * @author Guillaume Mary
 * 
 */
public class NOPServiceAgentFactory implements
		IServiceAgentFactory {
	/**
	 * Default constructor.
	 */
	public NOPServiceAgentFactory() {
	}

	/**
	 * {@inheritDoc}
	 */
	public IServiceAgent getAgent(String serviceName) {
		return NOPServiceAgent.NOP_SERVICE_AGENT;
	}

}
