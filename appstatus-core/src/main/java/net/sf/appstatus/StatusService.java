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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import net.sf.appstatus.core.IObjectInstantiationListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Richeton
 * 
 */
public class StatusService {
	private static final String CONFIG_LOCATION = "status-check.properties";

	private static Logger logger = LoggerFactory.getLogger(StatusService.class);

	private IObjectInstantiationListener objectInstanciationListener = null;
	private final List<IStatusChecker> probes;
	private final List<IPropertyProvider> propertyProviders;
	private IServletContextProvider servletContextProvider = null;

	/**
	 * Status Service creator.
	 */
	public StatusService() {

		probes = new ArrayList<IStatusChecker>();
		propertyProviders = new ArrayList<IPropertyProvider>();

	}

	public List<IStatusResult> checkAll() {

		ArrayList<IStatusResult> l = new ArrayList<IStatusResult>();

		for (IStatusChecker check : probes) {
			l.add(check.checkStatus());
		}
		return l;

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

	public IObjectInstantiationListener getObjectInstanciationListener() {
		return objectInstanciationListener;
	}

	public Map<String, Map<String, String>> getProperties() {
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

	public IServletContextProvider getServletContext() {
		return servletContextProvider;
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
					if (name.startsWith("check")) {
						String clazz = (String) p.get(name);
						IStatusChecker check = (IStatusChecker) getInstance(clazz);
						if (check instanceof IServletContextAware) {
							((IServletContextAware) check)
									.setServletContext(servletContextProvider
											.getServletContext());
						}
						probes.add(check);
						logger.info("Registered status checker " + clazz);
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
