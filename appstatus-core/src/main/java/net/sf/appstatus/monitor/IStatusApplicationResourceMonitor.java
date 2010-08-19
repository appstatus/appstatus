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
package net.sf.appstatus.monitor;

import java.util.List;
import java.util.Set;

import net.sf.appstatus.monitor.resource.IScheduledJobDetail;
import net.sf.appstatus.monitor.resource.IStatusExecutedJobResource;
import net.sf.appstatus.monitor.resource.IStatusJobRessource;

/**
 * Interface which describe the monitor of an application status.
 * 
 * @author Guillaume Mary
 * 
 */
public interface IStatusApplicationResourceMonitor extends
		IStatusResourceMonitor {

	/**
	 * Return the status of the last executed jobs.
	 * 
	 * @return status set of the last executed jobs
	 */
	List<IStatusExecutedJobResource> getLastExecutedJobsStatus();

	/**
	 * Return the next fired jobs.
	 * 
	 * @return next fired jobs
	 */
	List<IScheduledJobDetail> getNextFiredJobs();

	/**
	 * Return the set of running jobs status.
	 * 
	 * @return running jobs status
	 */
	Set<IStatusJobRessource> getRunningJobsStatus();
}
