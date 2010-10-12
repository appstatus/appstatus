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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import net.sf.appstatus.agent.batch.IBatchExecutionMonitor;

/**
 * A direct NOP (no operation) implementation of
 * {@link IBatchExecutionMonitor}.
 * 
 * @author Guillaume Mary
 * 
 */
public class NOPBatchExecutionMonitor implements IBatchExecutionMonitor {

	/**
	 * The unique instance of NOPJobProgressAgentMonitor.
	 */
	public static final NOPBatchExecutionMonitor NOP_BATCH_MONITOR = new NOPBatchExecutionMonitor();

	private static final List<String> NOP_LIST = new ArrayList<String>();

	private static final Date NOP_DATE = new Date();

	/**
	 * There is no point in creating multiple instances of
	 * NOPJobProgressMonitor, except by derived classes, hence the protected
	 * access for the constructor.
	 */
	protected NOPBatchExecutionMonitor() {
	}

	public Date getEndDate(String executionId) {
		return NOP_DATE;
	}

	public List<String> getJobExecutionIds() {
		return NOP_LIST;
	}

	public List<String> getLastMessages(String executionId, int nbMessage) {
		return NOP_LIST;
	}

	public double getProgressStatus(String executionId) {
		// NOP
		return 0;
	}

	public List<String> getRejectedItemsId(String executionId) {
		return NOP_LIST;
	}

	public Date getStartDate(String executionId) {
		return NOP_DATE;
	}

	public String getStatus(String executionId) {
		return "NOP";
	}

	public void update(Observable o, Object arg) {
		// NOP
	}

}
