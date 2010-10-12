/*
 * Copyright 2010 Capgemini Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sf.appstatus;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.sf.appstatus.core.IObjectInstantiationListener;
import net.sf.appstatus.monitor.Checker;
import net.sf.appstatus.monitor.Detail;
import net.sf.appstatus.monitor.DetailType;
import net.sf.appstatus.monitor.IStatusResourcesMonitor;
import net.sf.appstatus.monitor.Properties;
import net.sf.appstatus.monitor.Resource;
import net.sf.appstatus.monitor.ResourceType;
import net.sf.appstatus.monitor.Resources;
import net.sf.appstatus.monitor.resource.IStatusResourceMonitor;
import net.sf.appstatus.monitor.resource.batch.impl.BatchStatusResourceMonitor;
import net.sf.appstatus.monitor.resource.impl.BasicStatusResourceMonitor;
import net.sf.appstatus.monitor.resource.service.impl.ServiceStatusResourceMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Richeton
 * 
 */
public class StatusService implements IStatusResourcesMonitor {
	private static final String CONFIG_LOCATION = "monitored-resources.xml";

	private static Logger logger = LoggerFactory.getLogger(StatusService.class);

	private IObjectInstantiationListener objectInstanciationListener = null;

	private Set<IStatusResourceMonitor> monitoredResources;

	private IServletContextProvider servletContextProvider = null;

	/**
	 * Status Service creator.
	 */
	public StatusService() {
		monitoredResources = new HashSet<IStatusResourceMonitor>();
	}

	public int getGlobalStatus() {
		// for (IStatusResourceMonitor monitoredResource : monitoredResources) {
		// if (IStatusResult.OK != monitoredResource.getStatus().getCode()) {
		// return IStatusResult.ERROR;
		// }
		// }
		// return IStatusResult.OK;
		return 0;
	}

	private Object getInstance(String className) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		Object obj = null;

		if (objectInstanciationListener != null) {
			obj = objectInstanciationListener.getInstance(className);
		}

		if (obj != null) {
			return obj;
		}
		return Class.forName(className).newInstance();

	}

	public Set<IStatusResourceMonitor> getMonitoredResourcesStatus() {
		return monitoredResources;
	}

	public IObjectInstantiationListener getObjectInstanciationListener() {
		return objectInstanciationListener;
	}

	public IServletContextProvider getServletContext() {
		return servletContextProvider;
	}

	public void init() {
		try {
			// Load and init all probes
			Enumeration<URL> configFiles;

			// Load and init all probes
			ClassLoader classLoader = StatusService.class.getClassLoader();
			if (classLoader == null) {
				configFiles = ClassLoader.getSystemResources(CONFIG_LOCATION);
			} else {
				configFiles = classLoader.getResources(CONFIG_LOCATION);
			}

			if (configFiles == null) {
				return;
			}

			URL url = null;
			monitoredResources = new HashSet<IStatusResourceMonitor>();
			JAXBContext jc = JAXBContext
					.newInstance("net.sf.appstatus.monitor");
			Unmarshaller u = jc.createUnmarshaller();
			while (configFiles.hasMoreElements()) {
				url = configFiles.nextElement();

				// Load service configuration
				// Load resource configuration
				Resources o = (Resources) u.unmarshal(url);
				for (Resource resource : o.getResource()) {
					// init the checker set
					Set<IStatusChecker> statusCheckers = null;
					if (resource.getStatus() != null) {
						statusCheckers = new HashSet<IStatusChecker>();
						for (Checker checker : resource.getStatus()
								.getChecker()) {
							IStatusChecker check = (IStatusChecker) getInstance(checker
									.getClazz());
							if (check instanceof IServletContextAware) {
								((IServletContextAware) check)
										.setServletContext(servletContextProvider
												.getServletContext());
							}
							statusCheckers.add(check);
						}
					}

					// init the property provider set
					Set<IPropertyProvider> propertyProviders = null;
					if (resource.getConfiguration() != null) {
						propertyProviders = new HashSet<IPropertyProvider>();
						for (Properties property : resource.getConfiguration()
								.getProperties()) {
							IPropertyProvider provider = (IPropertyProvider) getInstance(property
									.getClazz());
							if (provider instanceof IServletContextAware
									&& servletContextProvider != null) {
								((IServletContextAware) provider)
										.setServletContext(servletContextProvider
												.getServletContext());
							}
							propertyProviders.add(provider);
						}
					}

					if (resource.getType().equals(ResourceType.BASIC)) {
						monitoredResources.add(new BasicStatusResourceMonitor(
								resource.getId(), resource.getName(), resource
										.getType().name(), statusCheckers,
								propertyProviders));
						logger.info(
								"The basic resource ({}) is registered in the system.",
								resource.getId());
					} else if (resource.getType().equals(ResourceType.SERVICE)) {
						String statisticsMonitorName = null;
						if (resource.getDetails() != null
								&& !resource.getDetails().getDetail().isEmpty()) {
							Detail detail = resource.getDetails().getDetail()
									.get(0);
							if (detail.getType().equals(DetailType.STATISTICS)) {
								statisticsMonitorName = detail.getMonitor();
							} else {
								logger.info(
										"The detail configuration {} of the resource ({}) is unknown by the system for a service resource. This detail configuration is ignored.",
										new Object[] { detail.getType(),
												resource.getId() });
							}
						}
						monitoredResources
								.add(new ServiceStatusResourceMonitor(resource
										.getId(), resource.getName(), resource
										.getType().name(), statusCheckers,
										propertyProviders,
										statisticsMonitorName));
						logger.info(
								"The service resource ({}) is registered in the system.",
								resource.getId());
					} else if (resource.getType().equals(ResourceType.BATCH)) {
						String nextMonitorName = null;
						String executionMonitorName = null;
						String historyMonitorName = null;
						if (!resource.getDetails().getDetail().isEmpty()) {
							// retrieve the monitor configured
							for (Detail detail : resource.getDetails()
									.getDetail()) {
								if (detail.getType().equals(
										DetailType.EXECUTION)) {
									executionMonitorName = detail.getMonitor();
								} else if (detail.getType().equals(
										DetailType.NEXT)) {
									nextMonitorName = detail.getMonitor();
								} else if (detail.getType().equals(
										DetailType.HISTORY)) {
									historyMonitorName = detail.getMonitor();
								} else {
									logger.info(
											"The detail configuration {} of the resource ({}) is unknown by the system for a batch resource. This detail configuration is ignored.",
											new Object[] { detail.getType(),
													resource.getId() });
								}
							}
						}
						monitoredResources.add(new BatchStatusResourceMonitor(
								resource.getId(), resource.getName(), resource
										.getType().name(), statusCheckers,
								propertyProviders, executionMonitorName,
								historyMonitorName, nextMonitorName));
						logger.info(
								"The batch resource ({}) is registered in the system.",
								resource.getId());
					} else {
						logger.info(
								"The configured resource ({}) type [{}] is unknow by the system. This resource is ignored.",
								new Object[] { resource.getId(),
										resource.getType().name() });
					}
				}
			}
		} catch (Exception e) {
			logger.error("Initialization error", e);
		}
	}

	public void setObjectInstanciationListener(
			IObjectInstantiationListener objectInstanciationListener) {
		this.objectInstanciationListener = objectInstanciationListener;
	}

	public void setServletContextProvider(IServletContextProvider servletContext) {
		this.servletContextProvider = servletContext;
	}
}
