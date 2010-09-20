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
package net.sf.appstatus.agent.batch.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.appstatus.agent.batch.IJobProgressAgent;
import net.sf.appstatus.agent.batch.IJobProgressAgentFactory;

/**
 * Memory progress agent factory.
 * 
 * @author Guillaume Mary
 * 
 */
public class MemJobProgressAgentFactory implements IJobProgressAgentFactory {

	/**
	 * Agents map
	 */
	private static Map<String, MemJobProgressAgent> agents = new ConcurrentHashMap<String, MemJobProgressAgent>();

	/**
	 * {@inheritDoc}
	 */
	public IJobProgressAgent getAgent(String executionId) {
		MemJobProgressAgent agent = null;
		if (agents.containsKey(executionId)) {
			agent = agents.get(executionId);
		} else {
			agent = new MemJobProgressAgent(executionId);
			agents.put(executionId, agent);
		}
		return agent;
	}

}
