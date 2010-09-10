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
package net.sf.appstatus.agent.batch.helpers;

import net.sf.appstatus.agent.batch.IJobProgressMonitorAgent;
import net.sf.appstatus.agent.batch.IJobProgressMonitorAgentFactory;

/**
 * Return the unique instance of the {@link NOPJobProgressMonitorAgent}
 * 
 * @author Guillaume Mary
 * 
 */
public class NOPJobProgressMonitorAgentFactory implements
		IJobProgressMonitorAgentFactory {
	/**
	 * Default constructor.
	 */
	public NOPJobProgressMonitorAgentFactory() {
	}

	/**
	 * {@inheritDoc}
	 */
	public IJobProgressMonitorAgent getAgent(String executionId) {
		return NOPJobProgressMonitorAgent.NOP_JOB_PROGRESS_MONITOR_AGENT;
	}

}
