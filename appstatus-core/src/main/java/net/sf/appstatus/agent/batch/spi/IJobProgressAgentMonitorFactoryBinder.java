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

import net.sf.appstatus.agent.batch.IJobProgressAgentMonitorFactory;
import net.sf.appstatus.agent.batch.JobProgressAgentMonitorFactory;

/**
 * An internal interface which helps the static
 * {@link JobProgressAgentMonitorFactory} class bind with the appropriate
 * {@link IJobProgressAgentMonitorFactory} instance.
 * 
 * @author Guillaume Mary
 */
public interface IJobProgressAgentMonitorFactoryBinder {

	/**
	 * Return the instance of {@link IJobProgressAgentMonitorFactory} that
	 * {@link JobProgressAgentMonitorFactory} class should bind to.
	 * 
	 * @return the instance of {@link IJobProgressAgentMonitorFactory} that
	 *         {@link JobProgressAgentMonitorFactory} class should bind to.
	 */
	IJobProgressAgentMonitorFactory getJobProgressAgentMonitorFactory();

	/**
	 * The String form of the {@link IJobProgressAgentMonitorFactory} object
	 * that this <code>IJobProgressAgentMonitorFactoryBinder</code> instance is
	 * <em>intended</em> to return.
	 * 
	 * @return the class name of the intended
	 *         {@link IJobProgressAgentMonitorFactory} instance
	 */
	String getJobProgressAgentMonitorFactoryStr();
}
