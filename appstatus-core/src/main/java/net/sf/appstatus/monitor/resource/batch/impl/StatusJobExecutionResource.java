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

import java.util.Date;
import java.util.List;

import net.sf.appstatus.IStatusResult;
import net.sf.appstatus.agent.batch.IJobProgressAgentMonitor;
import net.sf.appstatus.check.impl.StatusResultImpl;
import net.sf.appstatus.monitor.resource.ResourceType;
import net.sf.appstatus.monitor.resource.batch.IStatusJobExecutionResource;
import net.sf.appstatus.monitor.resource.batch.JobStatus;

/**
 * Status of a job execution.
 * 
 * @author Guillaume Mary
 * 
 */
public class StatusJobExecutionResource implements IStatusJobExecutionResource {

	private final String uid;

	private final String name;

	private final IJobProgressAgentMonitor jobProgressMonitor;

	/**
	 * Default constructor.
	 * 
	 * @param uid
	 *            job uid
	 * @param name
	 *            job name
	 * @param jobProgressAgentMonitor
	 *            job execution monitor
	 */
	public StatusJobExecutionResource(String uid, String name,
			IJobProgressAgentMonitor jobProgressAgentMonitor) {
		this.uid = uid;
		this.name = name;
		this.jobProgressMonitor = jobProgressAgentMonitor;
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getEndDate() {
		return jobProgressMonitor.getEndDate(uid);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getJobStatus() {
		return jobProgressMonitor.getStatus(uid);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getLastMessages(int nbMessage) {
		return jobProgressMonitor.getLastMessages(uid, nbMessage);
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
	public double getProgressStatus() {
		return jobProgressMonitor.getProgressStatus(uid);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getRejectedItemsId() {
		return jobProgressMonitor.getRejectedItemsId(uid);
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getStartDate() {
		return jobProgressMonitor.getStartDate(uid);
	}

	/**
	 * {@inheritDoc}
	 */
	public IStatusResult getStatus() {
		StatusResultImpl result = new StatusResultImpl();
		result.setProbeName(name + uid);
		if (getJobStatus().equals(JobStatus.FAILED.getLabel())
				|| getJobStatus().equals(JobStatus.ABANDONED.getLabel())) {
			result.setCode(IStatusResult.ERROR);
			result.setFatal(true);
			// retrieve the last message
			List<String> messages = getLastMessages(1);
			if (messages != null && !messages.isEmpty()) {
				result.setDescription(messages.get(0));
			}
		} else {
			result.setCode(IStatusResult.OK);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getType() {
		return ResourceType.JOB.getLabel();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUid() {
		return uid;
	}

}
