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
package net.sf.appstatus.agent.batch.spi;

import net.sf.appstatus.agent.batch.IJobProgressMonitorAgentFactory;
import net.sf.appstatus.agent.batch.JobProgressMonitorAgentFactory;

/**
 * An internal interface which helps the static
 * {@link JobProgressMonitorAgentFactory} class bind with the appropriate
 * {@link IJobProgressMonitorAgentFactory} instance.
 * 
 * @author Guillaume Mary
 */
public interface IJobProgressMonitorAgentFactoryBinder {

	/**
	 * Return the instance of {@link IJobProgressMonitorAgentFactory} that
	 * {@link JobProgressMonitorAgentFactory} class should bind to.
	 * 
	 * @return the instance of {@link IJobProgressMonitorAgentFactory} that
	 *         {@link JobProgressMonitorAgentFactory} class should bind to.
	 */
	IJobProgressMonitorAgentFactory getJobProgressMonitorAgentFactory();

	/**
	 * The String form of the {@link IJobProgressMonitorAgentFactory} object
	 * that this <code>JobProgressMonitorAgentFactoryBinder</code> instance is
	 * <em>intended</em> to return.
	 * 
	 * @return the class name of the intended
	 *         {@link IJobProgressMonitorAgentFactory} instance
	 */
	String getJobProgressMonitorAgentFactoryStr();
}
