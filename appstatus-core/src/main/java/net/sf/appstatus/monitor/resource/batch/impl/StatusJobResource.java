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
package net.sf.appstatus.monitor.resource.batch.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.appstatus.IStatusResult;
import net.sf.appstatus.agent.batch.BatchMonitorFactory;
import net.sf.appstatus.agent.batch.IBatchMonitor;
import net.sf.appstatus.check.impl.StatusResultImpl;
import net.sf.appstatus.monitor.resource.ResourceType;
import net.sf.appstatus.monitor.resource.batch.IScheduledJobDetail;
import net.sf.appstatus.monitor.resource.batch.IStatusExecutedJobResource;
import net.sf.appstatus.monitor.resource.batch.IStatusJobExecutionResource;
import net.sf.appstatus.monitor.resource.batch.IStatusJobResource;

/**
 * Status job resource.
 * 
 * @author Guillaume Mary
 * 
 */
public class StatusJobResource implements IStatusJobResource {

	private final String name;

	private final String uid;

	/**
	 * Default constructor.
	 * 
	 * @param uid
	 *            job uid
	 * @param name
	 *            job name
	 */
	public StatusJobResource(String uid, String name) {
		this.name = name;
		this.uid = uid;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IStatusJobExecutionResource> getCurrentJobExecutionsStatus() {
		List<IStatusJobExecutionResource> jobExecutionsStatus = new ArrayList<IStatusJobExecutionResource>();
		IBatchMonitor jobProgressMonitor = BatchMonitorFactory
				.getMonitor(getUid());
		List<String> executionIds = jobProgressMonitor.getJobExecutionIds();
		for (String executionId : executionIds) {
			jobExecutionsStatus.add(new StatusJobExecutionResource(executionId,
					getUid(), jobProgressMonitor));
		}
		return jobExecutionsStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IStatusExecutedJobResource> getLastExecutedJobsHistory() {
		throw new UnsupportedOperationException(
				"This method is not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScheduledJobDetail getNextJobExecution() {
		throw new UnsupportedOperationException(
				"This method is not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	public IStatusResult getStatus() {
		StatusResultImpl result = new StatusResultImpl();
		result.setProbeName(getName());
		result.setDescription("Global job execution status");
		result.setCode(IStatusResult.OK);
		List<IStatusJobExecutionResource> jobExecutions = getCurrentJobExecutionsStatus();
		for (IStatusJobExecutionResource jobExecution : jobExecutions) {
			if (IStatusResult.OK != jobExecution.getStatus().getCode()) {
				result.setCode(IStatusResult.ERROR);
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getType() {
		return ResourceType.BATCH.getLabel();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUid() {
		return uid;
	}

}
