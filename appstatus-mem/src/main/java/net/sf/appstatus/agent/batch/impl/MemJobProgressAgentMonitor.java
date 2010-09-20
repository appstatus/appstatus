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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import net.sf.appstatus.agent.batch.IJobProgressAgent;
import net.sf.appstatus.agent.batch.IJobProgressAgentMonitor;
import net.sf.appstatus.monitor.resource.batch.JobStatus;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * In memory job progress agent monitor.
 * 
 * @author Guillaume Mary
 * 
 */
public class MemJobProgressAgentMonitor implements IJobProgressAgentMonitor {

	public static final String RUNNING_JOBS_CACHE_NAME = "running_jobs";

	private final String jobName;

	public MemJobProgressAgentMonitor(String jobName) {
		this.jobName = jobName;
	}

	private double calculateSubTaskWorked(MemJobProgressAgent agent) {
		if (agent != null) {
			if (agent.getSubTasks() != null && !agent.getSubTasks().isEmpty()) {
				double worked = agent.getWorked();
				for (MemJobProgressAgent subTask : agent.getSubTasks()) {
					worked = worked + calculateSubTaskWorked(subTask);
				}
				return worked;
			} else if (IJobProgressAgent.UNKNOW != agent.getTotalWork()
					&& IJobProgressAgent.UNKNOW != agent.getWorked()
					&& IJobProgressAgent.UNKNOW != agent.getParentWork()
					&& agent.getParent() != null) {
				double progressParent = (double) agent.getWorked()
						/ agent.getTotalWork();
				return (agent.getParentWork() * progressParent);
			}
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	private void getAgentMessages(MemJobProgressAgent agent,
			List<String> messages) {
		// retrieve the agent messages
		if (agent.getMessages() != null) {
			messages.addAll(agent.getMessages());
		}

		// if the agent has sub task
		if (agent.getSubTasks() != null && !agent.getSubTasks().isEmpty()) {
			// retrieve the agent subtask messages
			for (MemJobProgressAgent subAgent : agent.getSubTasks()) {
				getAgentMessages(subAgent, messages);
			}
		}
	}

	private void getAgentRejectedItemIds(MemJobProgressAgent agent,
			List<String> rejectedItemsId) {
		// retrieve the agent rejected items
		if (agent.getRejectedItems() != null) {
			for (RejectedItem rejectedItem : agent.getRejectedItems()) {
				rejectedItemsId.add(rejectedItem.getId());
			}
		}

		// if the agent has sub task
		if (agent.getSubTasks() != null && !agent.getSubTasks().isEmpty()) {
			// retrieve the agent subtask rejected ids
			for (MemJobProgressAgent subAgent : agent.getSubTasks()) {
				getAgentRejectedItemIds(subAgent, rejectedItemsId);
			}
		}
	}

	/**
	 * Retrieve the running job cache.
	 * 
	 * @return running jobs cache
	 */
	private Cache getCache(String cacheName) {
		if (!CacheManager.getInstance().cacheExists(cacheName)) {
			CacheManager.getInstance().addCache(cacheName);
		}
		Cache cache = CacheManager.getInstance().getCache(cacheName);
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getEndDate(String executionId) {
		Element elt = getCache(RUNNING_JOBS_CACHE_NAME).get(
				getRunningJobCacheKey(executionId));
		if (elt != null) {
			MemJobProgressAgent agent = (MemJobProgressAgent) elt.getValue();
			if (agent.getEndTimestamp() != 0) {
				return new Date(agent.getEndTimestamp());
			}
		}
		return null;
	}

	private String getExecutionIdFromRunningJobCacheKey(Object key) {
		return ((String) key).substring(jobName.length() + 1);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getJobExecutionIds() {
		List<String> jobExecutionIds = new ArrayList<String>();
		for (Object key : getCache(RUNNING_JOBS_CACHE_NAME)
				.getKeysWithExpiryCheck()) {
			jobExecutionIds.add(getExecutionIdFromRunningJobCacheKey(key));
		}
		return jobExecutionIds;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getLastMessages(String executionId, int nbMessage) {
		List<String> messages = new ArrayList<String>();
		Element elt = getCache(RUNNING_JOBS_CACHE_NAME).get(
				getRunningJobCacheKey(executionId));
		if (elt != null) {
			MemJobProgressAgent agent = (MemJobProgressAgent) elt.getValue();
			getAgentMessages(agent, messages);
		}
		return messages;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getProgressStatus(String executionId) {
		Element elt = getCache(RUNNING_JOBS_CACHE_NAME).get(
				getRunningJobCacheKey(executionId));
		if (elt != null) {
			MemJobProgressAgent agent = (MemJobProgressAgent) elt.getValue();
			double worked = agent.getWorked();
			worked = worked + calculateSubTaskWorked(agent);
			return worked / agent.getTotalWork();
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<String> getRejectedItemsId(String executionId) {
		List<String> rejectedItemsId = new ArrayList<String>();
		Element elt = getCache(RUNNING_JOBS_CACHE_NAME).get(
				getRunningJobCacheKey(executionId));
		if (elt != null) {
			MemJobProgressAgent agent = (MemJobProgressAgent) elt.getValue();
			getAgentRejectedItemIds(agent, rejectedItemsId);
		}
		return rejectedItemsId;
	}

	private String getRunningJobCacheKey(String executionId) {
		return jobName + "." + executionId;
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getStartDate(String executionId) {
		Element elt = getCache(RUNNING_JOBS_CACHE_NAME).get(
				getRunningJobCacheKey(executionId));
		if (elt != null) {
			MemJobProgressAgent agent = (MemJobProgressAgent) elt.getValue();
			if (agent.getStartTimestamp() != 0) {
				return new Date(agent.getStartTimestamp());
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getStatus(String executionId) {
		String status = JobStatus.UNKNOW.getLabel();
		Element elt = getCache(RUNNING_JOBS_CACHE_NAME).get(
				getRunningJobCacheKey(executionId));
		if (elt != null) {
			MemJobProgressAgent agent = (MemJobProgressAgent) elt.getValue();
			if (agent.getStatus() != null) {
				status = agent.getStatus();
			}
		}
		return status;
	}

	private void removeJobExecution(MemJobProgressAgent agent) {
		Cache cache = getCache(RUNNING_JOBS_CACHE_NAME);
		cache.remove(agent.getExecutionId());
	}

	private void storeNewJobExecution(MemJobProgressAgent agent) {
		// store the agent in the cache
		if (agent.getParent() == null) {
			Cache cache = getCache(RUNNING_JOBS_CACHE_NAME);
			cache.put(new Element(
					getRunningJobCacheKey(agent.getExecutionId()), agent));
		}
		// else the execution is already stored
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(Observable o, Object arg) {
		if (o instanceof MemJobProgressAgent) {
			if (arg instanceof String) {
				MemJobProgressAgent agent = (MemJobProgressAgent) o;
				if (MemJobProgressAgent.BEGIN_TASK_ACTION.equals(arg)) {
					storeNewJobExecution(agent);
				} else if (MemJobProgressAgent.END_TASK_ACTION.equals(arg)) {
					removeJobExecution(agent);
				} else {
					updateJobExecution(agent);
				}
			}
		}
	}

	private void updateJobExecution(MemJobProgressAgent agent) {
		Cache cache = getCache(RUNNING_JOBS_CACHE_NAME);
		cache.put(new Element(getRunningJobCacheKey(agent.getExecutionId()),
				agent));
	}

}
