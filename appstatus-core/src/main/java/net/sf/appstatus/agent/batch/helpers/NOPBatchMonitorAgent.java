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

import net.sf.appstatus.agent.batch.IBatchMonitorAgent;

/**
 * A direct NOP (no operation) implementation of {@link IBatchMonitorAgent}.
 * 
 * @author Guillaume Mary
 * 
 */
public class NOPBatchMonitorAgent implements IBatchMonitorAgent {

	/**
	 * The unique instance of NOPJobProgressAgent.
	 */
	public static final NOPBatchMonitorAgent NOP_BATCH_AGENT = new NOPBatchMonitorAgent();

	/**
	 * There is no point in creating multiple instances of
	 * NOPJobProgressMonitorAgent, except by derived classes, hence the
	 * protected access for the constructor.
	 */
	protected NOPBatchMonitorAgent() {
	}

	/**
	 * {@inheritDoc}
	 */
	public void beginTask(String name, String group, String description,
			int totalWork) {
		// NOP implementation
	}

	/**
	 * {@inheritDoc}
	 */
	public IBatchMonitorAgent createSubTask(int work) {
		return NOP_BATCH_AGENT;
	}

	/**
	 * {@inheritDoc}
	 */
	public void done() {
		// NOP implementation
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTotalWork() {
		// NOP implementation
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public void message(String message) {
		// NOP implementation
	}

	/**
	 * {@inheritDoc}
	 */
	public void reject(Object item, String reason, String idMethodName) {
		// NOP implementation
	}

	/**
	 * {@inheritDoc}
	 */
	public void reject(Object[] items, String reason, String idMethodName) {
		// NOP implementation
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCurrentItem(Object item) {
		// NOP implementation
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTotalWork(int totalWork) {
		// NOP implementation
	}

	/**
	 * {@inheritDoc}
	 */
	public void worked(int work) {
		// NOP implementation
	}

}
