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

import java.util.List;

import net.sf.appstatus.agent.batch.IJobProgressMonitorAgent;
import net.sf.appstatus.agent.batch.JobProgressMonitorAgentFactory;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.AfterRead;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.annotation.OnProcessError;
import org.springframework.batch.core.annotation.OnReadError;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.batch.core.annotation.OnWriteError;

/**
 * Spring job listener used to monitor the spring job execution.
 * 
 * @author Guillaume Mary
 * 
 */
public class SpringJobMonitorAgentExecutionListener {

	/**
	 * Job description.
	 */
	private String description;

	/**
	 * Job group.
	 */
	private String group;

	/**
	 * The current job monitor.
	 */
	private IJobProgressMonitorAgent jobMonitor;

	/**
	 * Job name.
	 */
	private String name;

	/**
	 * Job step monitor.
	 */
	private IJobProgressMonitorAgent stepMonitor = null;

	/**
	 * Total work, usually the number of steps.
	 */
	int totalWork = IJobProgressMonitorAgent.UNKNOW;

	/**
	 * After job execution callback method.
	 * 
	 * @param jobExecution
	 *            job execution.
	 */
	@AfterJob
	public void afterJob(JobExecution jobExecution) {
		jobMonitor.done();
	}

	@AfterRead
	public void afterRead(Object item) {
		if (item != null) {
			// increment item read
			int totalWork = stepMonitor.getTotalWork();
			if (totalWork == IJobProgressMonitorAgent.UNKNOW) {
				totalWork = 1;
			} else {
				totalWork++;
			}
			stepMonitor.setTotalWork(totalWork);
		}
	}

	/**
	 * After the step execution callback method.
	 * 
	 * @param stepExecution
	 *            step execution
	 * @return step exit status
	 */
	@AfterStep
	public ExitStatus afterStep(StepExecution stepExecution) {
		stepMonitor.done();
		return stepExecution.getExitStatus();
	}

	/**
	 * After write items execution
	 * 
	 * @param items
	 *            items
	 */
	@AfterWrite
	public void afterWrite(List<? extends Object> items) {
		stepMonitor.worked(items.size());
	}

	/**
	 * Before the job execution.
	 * 
	 * @param jobExecution
	 *            job execution object
	 */
	@BeforeJob
	public void beforeJob(JobExecution jobExecution) {
		jobMonitor = JobProgressMonitorAgentFactory.getAgent(jobExecution
				.getJobId().toString());
		jobMonitor.beginTask(name, group, description, totalWork);
	}

	/**
	 * Before the step execution.
	 * 
	 * @param stepExecution
	 *            step execution.
	 */
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		stepMonitor = jobMonitor.createSubTask(1);
		stepMonitor.beginTask(stepExecution.getStepName(), group,
				stepExecution.getSummary(), IJobProgressMonitorAgent.UNKNOW);
	}

	/**
	 * On error during a process.
	 * 
	 * @param item
	 *            item
	 * @param e
	 *            exception
	 */
	@OnProcessError
	public void onProcessError(Object item, Exception e) {
		stepMonitor.message("Process error : " + item + " cause : "
				+ e.getLocalizedMessage());
	}

	/**
	 * On read error.
	 * 
	 * @param exception
	 *            exception
	 */
	@OnReadError
	public void onReadError(Exception exception) {
		stepMonitor.message("Read error : " + exception.getLocalizedMessage());
	}

	/**
	 * On skip item in process execution.
	 * 
	 * @param item
	 *            item skipped
	 * @param cause
	 *            cause
	 */
	@OnSkipInProcess
	public void onSkipInProcess(Object item, Throwable cause) {
		stepMonitor.reject(item, cause.getLocalizedMessage());
	}

	/**
	 * On skip item in read execution.
	 * 
	 * @param cause
	 *            cause
	 */
	@OnSkipInRead
	public void onSkipInRead(Throwable cause) {
		stepMonitor.reject(null, cause.getLocalizedMessage());
	}

	/**
	 * On skip item in write execution.
	 * 
	 * @param item
	 *            item
	 * @param cause
	 *            cause
	 */
	@OnSkipInWrite
	public void onSkipInWrite(Object item, Throwable cause) {
		stepMonitor.reject(item, cause.getLocalizedMessage());
	}

	/**
	 * On write error.
	 * 
	 * @param exception
	 *            exception
	 * @param items
	 *            items
	 */
	@OnWriteError
	public void onWriteError(Exception exception, List<? extends Object> items) {
		stepMonitor.message("Write error : " + exception.getLocalizedMessage()
				+ " items : " + items.toString());
	}

	/**
	 * Set the description.
	 * 
	 * @param description
	 *            description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Set the group.
	 * 
	 * @param group
	 *            group
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * Set the name.
	 * 
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the total work.
	 * 
	 * @param totalWork
	 *            total work.
	 */
	public void setTotalWork(int totalWork) {
		this.totalWork = totalWork;
	}

}
