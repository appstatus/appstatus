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
package net.sf.appstatus.monitor.resource.batch;

import java.util.List;

import net.sf.appstatus.monitor.resource.IStatusResource;

/**
 * Job resource status.
 * 
 * @author Guillaume Mary
 * 
 */
public interface IStatusJobResource extends IStatusResource {
	/**
	 * Retrieve the current running job
	 * 
	 * @return the current job's executions
	 */
	List<IStatusJobExecutionResource> getCurrentJobExecutionsStatus();

	/**
	 * Retrieve the last executed jobs history.
	 * 
	 * @return the last executed job history
	 */
	List<IStatusExecutedJobResource> getLastExecutedJobsHistory();

	/**
	 * Retrieve the next sheduled job execution.
	 * 
	 * @return next scheduled job execution
	 */
	IScheduledJobDetail getNextJobExecution();

}
