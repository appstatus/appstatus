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
package net.sf.appstatus.monitor.resource.service.statistics.spi;

import net.sf.appstatus.monitor.resource.service.statistics.IServiceMonitorStatisticsProviderFactory;
import net.sf.appstatus.monitor.resource.service.statistics.ServiceMonitorStatisticsProviderFactory;

/**
 * An internal interface which helps the static
 * {@link ServiceMonitorStatisticsProviderFactory} class bind with the
 * appropriate {@link IServiceMonitorStatisticsProviderFactory} instance.
 * 
 * @author Guillaume Mary
 */
public interface IServiceMonitorStatisticsProviderFactoryBinder {

	/**
	 * Return the instance of {@link IServiceMonitorStatisticsProviderFactory}
	 * that {@link ServiceMonitorStatisticsProviderFactory} class should bind
	 * to.
	 * 
	 * @return the instance of {@link IServiceMonitorStatisticsProviderFactory}
	 *         that {@link ServiceMonitorStatisticsProviderFactory} class should
	 *         bind to.
	 */
	IServiceMonitorStatisticsProviderFactory getServiceMonitorStatisticsProviderFactory();

	/**
	 * The String form of the {@link IServiceMonitorStatisticsProviderFactory}
	 * object that this
	 * <code>ServiceMonitorStatisticsProviderFactoryBinder</code> instance is
	 * <em>intended</em> to return.
	 * 
	 * @return the class name of the intended
	 *         {@link IServiceMonitorStatisticsProviderFactory} instance
	 */
	String getServiceMonitorStatisticsProviderFactoryStr();
}
