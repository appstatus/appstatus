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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import net.sf.appstatus.core.IObjectInstantiationListener;
import net.sf.appstatus.monitor.IStatusApplicationResourceMonitor;
import net.sf.appstatus.monitor.resource.IStatusResource;
import net.sf.appstatus.monitor.resource.ResourceType;
import net.sf.appstatus.monitor.resource.batch.IScheduledJobDetail;
import net.sf.appstatus.monitor.resource.batch.IStatusExecutedJobResource;
import net.sf.appstatus.monitor.resource.batch.IStatusJobResource;
import net.sf.appstatus.monitor.resource.batch.impl.StatusJobResource;
import net.sf.appstatus.monitor.resource.impl.StatusResource;
import net.sf.appstatus.monitor.resource.service.IStatusServiceResource;
import net.sf.appstatus.monitor.resource.service.impl.StatusServiceResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Richeton
 * 
 */
public class StatusService implements IStatusApplicationResourceMonitor {
	private static final String CONFIG_LOCATION = "status-check.properties";

	private static Logger logger = LoggerFactory.getLogger(StatusService.class);

	private IObjectInstantiationListener objectInstanciationListener = null;
	private final List<IPropertyProvider> propertyProviders;
	private final Set<IStatusResource> monitoredResources;
	private IServletContextProvider servletContextProvider = null;

	/**
	 * Status Service creator.
	 */
	public StatusService() {
		monitoredResources = new HashSet<IStatusResource>();
		propertyProviders = new ArrayList<IPropertyProvider>();
	}

	public Map<String, Map<String, String>> getConfiguration() {
		TreeMap<String, Map<String, String>> categories = new TreeMap<String, Map<String, String>>();

		for (IPropertyProvider provider : propertyProviders) {
			if (categories.get(provider.getCategory()) == null) {
				categories.put(provider.getCategory(),
						new TreeMap<String, String>());
			}

			Map<String, String> l = categories.get(provider.getCategory());

			l.putAll(provider.getProperties());
		}
		return categories;
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

	public List<IStatusExecutedJobResource> getLastExecutedJobsStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IStatusResource> getMonitoredResourcesStatus() {
		return monitoredResources;
	}

	public List<IScheduledJobDetail> getNextFiredJobs() {
		// TODO Auto-generated method stub
		return null;
	}

	public IObjectInstantiationListener getObjectInstanciationListener() {
		return objectInstanciationListener;
	}

	public String getRessourceName() {
		String resourceName = "ROOT";
		IServletContextProvider servletProvider = getServletContext();
		if (servletProvider != null) {
			resourceName = servletProvider.getServletContext().getServerInfo();
		}
		return resourceName;
	}

	public Set<IStatusJobResource> getRunningJobsStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	public IServletContextProvider getServletContext() {
		return servletContextProvider;
	}

	public int getStatus() {
		for (IStatusResource monitoredResource : monitoredResources) {
			if (IStatusResult.OK != monitoredResource.getStatus().getCode()) {
				return IStatusResult.ERROR;
			}
		}
		return IStatusResult.OK;
	}

	public void init() {
		try {
			// Load and init all probes
			Enumeration<URL> configFiles;

			configFiles = StatusService.class.getClassLoader().getResources(
					CONFIG_LOCATION);

			if (configFiles == null) {
				return;
			}

			URL url = null;
			Properties p = null;
			InputStream is = null;
			Map<String, IStatusResource> mapResource = new HashMap<String, IStatusResource>();
			while (configFiles.hasMoreElements()) {
				url = configFiles.nextElement();

				// Load plugin configuration
				p = new Properties();
				is = url.openStream();
				p.load(is);
				is.close();

				Set<Object> keys = p.keySet();
				String name = null;
				for (Object key : keys) {
					name = (String) key;
					if (name.startsWith("resource.check")) {
						String clazz = (String) p.get(name);
						IStatusChecker check = (IStatusChecker) getInstance(clazz);
						if (check instanceof IServletContextAware) {
							((IServletContextAware) check)
									.setServletContext(servletContextProvider
											.getServletContext());
						}
						String realName = name.substring("resource.check."
								.length());
						IStatusResource resource = new StatusResource(realName,
								ResourceType.DEFAULT.getLabel(), check);
						monitoredResources.add(resource);
						logger.info(
								"Registered status resource {} with checker : {}",
								resource.getName(), clazz);
					} else if (name.startsWith("property")) {
						String clazz = (String) p.get(name);
						IPropertyProvider provider = (IPropertyProvider) getInstance(clazz);
						propertyProviders.add(provider);
						if (provider instanceof IServletContextAware
								&& servletContextProvider != null) {
							((IServletContextAware) provider)
									.setServletContext(servletContextProvider
											.getServletContext());
						}
						// else : guess is if we don't have the servlet context
						// now, we will
						// later.
						logger.info("Registered property provider " + clazz);
					} else if (name.startsWith("service.class")) {
						String clazz = (String) p.get(name);
						String realName = name.substring("service.class."
								.length());
						IStatusServiceResource resource = new StatusServiceResource(
								clazz, realName);
						mapResource.put(realName, resource);
						logger.info(
								"Registered service resource : {}, class={}",
								realName, clazz);
					} else if (name.startsWith("service.check")) {
						String realName = name.substring("service.check."
								.length());
						String clazz = (String) p.get(name);
						if (mapResource.containsKey(realName)) {
							IStatusChecker check = (IStatusChecker) getInstance(clazz);
							if (check instanceof IServletContextAware) {
								((IServletContextAware) check)
										.setServletContext(servletContextProvider
												.getServletContext());
							}
							StatusServiceResource resource = (StatusServiceResource) mapResource
									.get(realName);
							resource.setStatusChecker(check);
							mapResource.put(realName, resource);
							logger.info(
									"Add status checker ({}) to a registered service ({})",
									clazz, realName);
						} else {
							logger.error(
									"A checker ({}) is configured for an unregistered service ({}). This configuration is discarded.",
									clazz, realName);
						}
					} else if (name.startsWith("batch.name")) {
						String jobName = (String) p.get(name);
						String uid = name.substring("batch.name.".length());
						IStatusJobResource resource = new StatusJobResource(
								uid, jobName);
						mapResource.put(uid, resource);
						logger.info("Registered batch resource : {}, name={}",
								uid, jobName);
					}
					// add all the registered resource
					monitoredResources.addAll(mapResource.values());
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
