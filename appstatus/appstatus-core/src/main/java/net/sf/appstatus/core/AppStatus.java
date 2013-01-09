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
package net.sf.appstatus.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchManager;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;
import net.sf.appstatus.core.check.ICheck;
import net.sf.appstatus.core.check.ICheckResult;
import net.sf.appstatus.core.property.IPropertyProvider;
import net.sf.appstatus.core.services.IService;
import net.sf.appstatus.core.services.IServiceManager;
import net.sf.appstatus.core.services.IServiceMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the entry point for every feature of AppStatus.
 * 
 * <p>
 * Must be initialized once before calling other methods.
 * <p>
 * AppStatus status = new AppStatus(); <br/>
 * status.init();
 * </p>
 * 
 * @author Nicolas Richeton
 * 
 */
public class AppStatus {
	private static final String CONFIG_LOCATION = "status-check.properties";

	private static Logger logger = LoggerFactory.getLogger(AppStatus.class);

	private IBatchManager batchManager = null;
	protected List<ICheck> checkers;
	private Properties configuration = null;
	private boolean initDone = false;
	private IObjectInstantiationListener objectInstanciationListener = null;
	private List<IPropertyProvider> propertyProviders;
	private IServiceManager serviceManager = null;
	private IServletContextProvider servletContextProvider = null;

	/**
	 * Status Service creator.
	 */
	public AppStatus() {
	}

	private void addPropertyProvider(String clazz) {
		IPropertyProvider provider = (IPropertyProvider) getClassInstance(clazz);

		if (provider != null) {
			propertyProviders.add(provider);
			logger.info("Registered property provider " + clazz);
		} else {
			logger.error("cannot instanciate class {}, Please configure \"{}\" file properly", clazz, CONFIG_LOCATION);
		}
	}

	private void addStatusChecker(String clazz) {
		ICheck check = (ICheck) getClassInstance(clazz);
		if (check == null) {
			logger.error("cannot instanciate class {}, Please configure \"{}\" file properly", clazz, CONFIG_LOCATION);
			return;
		}

		if (check instanceof IServletContextAware) {
			((IServletContextAware) check).setServletContext(servletContextProvider.getServletContext());
		}

		checkers.add(check);
		logger.info("Registered status checker " + clazz);
	}

	public List<ICheckResult> checkAll() {
		checkInit();

		ArrayList<ICheckResult> statusList = new ArrayList<ICheckResult>();
		for (ICheck check : checkers) {
			injectServletContext(check);
			statusList.add(check.checkStatus());
		}
		return statusList;
	}

	private void checkInit() {
		if (!initDone) {
			logger.warn("Not initialized. Starting init");
			init();

		}
	}

	public IBatchManager getBatchManager() {
		return batchManager;
	}

	public IBatchProgressMonitor getBatchProgressMonitor(String name, String group, String uuid) {

		checkInit();

		IBatch batch = null;
		if (batchManager != null) {
			batch = batchManager.addBatch(name, group, uuid);
			return batchManager.getMonitor(batch);
		}

		return null;
	}

	/**
	 * Try to instantiate a class.
	 * 
	 * @param className
	 * @return an object instance of "className" class or null if instantiation
	 *         is not possible
	 */
	private Object getClassInstance(String className) {
		Object obj = null;

		if (objectInstanciationListener != null) {
			obj = objectInstanciationListener.getInstance(className);
		}

		if (obj == null) {
			try {
				obj = Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
			} catch (ClassNotFoundException e) {
				logger.warn("Class {} not found ", className, e);
			} catch (InstantiationException e) {
				logger.warn("Cannot instanciate {} ", className, e);
			} catch (IllegalAccessException e) {
				logger.warn("Cannot access class {} for instantiation ", className, e);
			}
		}

		if (obj == null) {
			try {
				obj = Class.forName(className).newInstance();
				logger.warn(
						"Class {} loaded using a deprecated method. Please report to http://sourceforge.net/apps/mantisbt/appstatus/login_select_proj_page.php?ref=bug_report_page.php",
						className);
			} catch (ClassNotFoundException e) {
				logger.warn("Class {} not found ", className, e);
			} catch (InstantiationException e) {
				logger.warn("Cannot instanciate {} ", className, e);
			} catch (IllegalAccessException e) {
				logger.warn("Cannot access class {} for instantiation ", className, e);
			}
		}

		if (obj != null) {
			injectServletContext(obj);
		}

		return obj;
	}

	public Map<String, Map<String, String>> getProperties() {
		checkInit();

		TreeMap<String, Map<String, String>> categories = new TreeMap<String, Map<String, String>>();

		for (IPropertyProvider provider : propertyProviders) {
			injectServletContext(provider);

			// Init Category
			if (categories.get(provider.getCategory()) == null) {
				categories.put(provider.getCategory(), new TreeMap<String, String>());
			}

			// Add all properties
			Map<String, String> l = categories.get(provider.getCategory());
			l.putAll(provider.getProperties());
		}
		return categories;
	}

	@Deprecated
	public List<IBatch> getRunningBatches() {
		return batchManager.getRunningBatches();
	}

	public IServiceManager getServiceManager() {
		return serviceManager;
	}

	public IServiceMonitor getServiceMonitor(String name, String group) {
		checkInit();

		IService service = null;
		if (serviceManager != null) {
			service = serviceManager.getService(name, group);
			return serviceManager.getMonitor(service);
		}

		return null;
	}

	public List<IService> getServices() {
		if (serviceManager == null) {
			return null;
		}

		return serviceManager.getServices();
	}

	public IServletContextProvider getServletContext() {
		return servletContextProvider;
	}

	public synchronized void init() {

		if (initDone) {
			logger.warn("Already initialized");
			return;
		}

		// Load plugins
		loadPlugins();

		// Test some values where injected
		if (checkers != null || propertyProviders != null || configuration != null) {
			logger.info("Configuration is injected : skip loading properties from classpath");
		} else {

			// Init
			checkers = new ArrayList<ICheck>();
			propertyProviders = new ArrayList<IPropertyProvider>();
			configuration = new Properties();

			try {

				// Load and init all probes
				Enumeration<URL> configFiles;

				configFiles = Thread.currentThread().getContextClassLoader().getResources(CONFIG_LOCATION);

				if (configFiles == null) {
					logger.info("config file {} not found in classpath", CONFIG_LOCATION);
					return;
				}

				while (configFiles.hasMoreElements()) {
					Properties p = loadProperties(configFiles.nextElement());

					configuration.putAll(p);

					Set<Object> keys = p.keySet();
					for (Object oName : keys) {
						String name = (String) oName;
						String clazz = (String) p.get(name);
						if (name.startsWith("check")) {
							addStatusChecker(clazz);
						} else if (name.startsWith("property")) {
							addPropertyProvider(clazz);
						} else {
							logger.warn("unknown propery  {} : {} ", name, clazz);
						}
					}
				}

			} catch (Exception e) {
				logger.error("Initialization error", e);
			}
		}

		// Give all configuration properties to managers.
		if (getBatchManager() != null) {
			getBatchManager().setConfiguration(configuration);
		}
		if (getServiceManager() != null) {
			getServiceManager().setConfiguration(configuration);
		}

		initDone = true;

	}

	private void injectServletContext(Object instance) {
		// Inject servlet context if possible
		if (instance instanceof IServletContextAware && servletContextProvider != null) {
			((IServletContextAware) instance).setServletContext(servletContextProvider.getServletContext());
		}
	}

	private void loadPlugins() {
		int count = 0;
		try {
			Enumeration<URL> plugins = Thread.currentThread().getContextClassLoader()
					.getResources("net/sf/appstatus/plugin.properties");

			while (plugins.hasMoreElements()) {
				URL url = plugins.nextElement();
				logger.info("AppStatus: found plugin: " + url.toString());
				Properties p = loadProperties(url);

				// batchManager
				String batchManagerClass = p.getProperty("batchManager");
				if (batchManagerClass != null) {
					batchManager = (IBatchManager) getClassInstance(batchManagerClass);
				}

				// serviceManager
				String serviceManagerClass = p.getProperty("serviceManager");
				if (serviceManagerClass != null) {
					serviceManager = (IServiceManager) getClassInstance(serviceManagerClass);
				}
				count++;
			}
		} catch (IOException e) {
			logger.warn("AppStatus: Error loading plugins", e);
		}
		logger.info("AppStatus: found {} plugins", count);
	}

	/**
	 * Load a properties file from a given URL.
	 * 
	 * @param url
	 *            an url
	 * @return a {@link Properties} object
	 * @throws IOException
	 *             in an error occurs
	 */
	private Properties loadProperties(URL url) throws IOException {
		// Load plugin configuration
		Properties p = new Properties();
		InputStream is = url.openStream();
		p.load(is);
		is.close();
		return p;
	}

	public void setCheckers(List<ICheck> checkers) {
		this.checkers = checkers;
	}

	public void setConfiguration(Properties configuration) {
		this.configuration = configuration;
	}

	public void setObjectInstanciationListener(IObjectInstantiationListener objectInstanciationListener) {
		this.objectInstanciationListener = objectInstanciationListener;
	}

	public void setPropertyProviders(List<IPropertyProvider> propertyProviders) {
		this.propertyProviders = propertyProviders;
	}

	public void setServletContextProvider(IServletContextProvider servletContext) {
		this.servletContextProvider = servletContext;
	}
}
