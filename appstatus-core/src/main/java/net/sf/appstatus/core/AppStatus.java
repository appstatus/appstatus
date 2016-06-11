/*
 * Copyright 2010 Capgemini and Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the
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
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.appstatus.core.batch.IBatch;
import net.sf.appstatus.core.batch.IBatchManager;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;
import net.sf.appstatus.core.batch.IBatchScheduleManager;
import net.sf.appstatus.core.check.CheckResultBuilder;
import net.sf.appstatus.core.check.IAppStatusAware;
import net.sf.appstatus.core.check.ICheck;
import net.sf.appstatus.core.check.ICheckResult;
import net.sf.appstatus.core.check.IConfigurationAware;
import net.sf.appstatus.core.loggers.ILoggersManager;
import net.sf.appstatus.core.property.IPropertyProvider;
import net.sf.appstatus.core.services.IService;
import net.sf.appstatus.core.services.IServiceManager;
import net.sf.appstatus.core.services.IServiceMonitor;

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
	private IBatchScheduleManager batchScheduleManager;
	protected List<ICheck> checkers;
	private Properties configuration = null;
	private ExecutorService executorService = null;
	private boolean initDone = false;
	private ILoggersManager loggersManager = null;
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
		logger.info("Registered status checker {}", clazz);
	}

	public List<ICheckResult> checkAll() {
		return checkAll(null);
	}

	public List<ICheckResult> checkAll(final Locale locale) {
		checkInit();

		List<Future<ICheckResult>> statusFutureList = new ArrayList<Future<ICheckResult>>();
		for (final ICheck check : checkers) {
			injectServletContext(check);

			statusFutureList.add(executorService.submit(new Callable<ICheckResult>() {
				public ICheckResult call() throws Exception {
					// Inject Appstatus
					if (check instanceof IAppStatusAware) {
						((IAppStatusAware) check).setAppStatus(AppStatus.this);
					}

					// Inject configuration
					if (check instanceof IConfigurationAware) {
						((IConfigurationAware) check).setConfiguration(getConfiguration());
					}
					return check.checkStatus(locale);
				}
			}));
		}

		ArrayList<ICheckResult> statusList = new ArrayList<ICheckResult>();

		for (int i = 0; i < statusFutureList.size(); i++) {
			Future<ICheckResult> f = statusFutureList.get(i);
			ICheck c = checkers.get(i);

			try {
				statusList.add(f.get());
			} catch (InterruptedException e) {
				statusList.add(createCheckResultFromException(c, e));
				logger.error("", e);
			} catch (ExecutionException e) {
				statusList.add(createCheckResultFromException(c, e));
				logger.error("", e);
			}
		}

		return statusList;
	}

	private void checkInit() {
		if (!initDone) {
			logger.warn("Not initialized. Starting init");
			init();
		}
	}

	private ICheckResult createCheckResultFromException(ICheck c, Exception e) {

		return new CheckResultBuilder().from(c).code(ICheckResult.ERROR).fatal()
				.description("Check failed with exception: " + e.getClass().getCanonicalName() + " " + e.getMessage())
				.build();

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
	 * @return the batchScheduleManager
	 */
	public IBatchScheduleManager getBatchScheduleManager() {
		return batchScheduleManager;
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

	protected Properties getConfiguration() {
		return this.configuration;
	}

	public ILoggersManager getLoggersManager() {
		return loggersManager;
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
		executorService = Executors.newCachedThreadPool();

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
							logger.debug("Global property  {} : {} ", name, clazz);
						}
					}
				}

			} catch (Exception e) {
				logger.error("Initialization error", e);
			}
		}

		// If configuration is null (Spring with no configuration block), create
		// empty object
		if (configuration == null) {
			configuration = new Properties();
		}

		// Give all configuration properties to managers.
		if (getBatchManager() != null) {
			Properties newConfiguration = getBatchManager().getConfiguration();

			if (newConfiguration != null) {
				newConfiguration.putAll(configuration);
			} else {
				newConfiguration = configuration;
			}

			getBatchManager().setConfiguration(newConfiguration);
		}
		if (getServiceManager() != null) {
			Properties newConfiguration = getServiceManager().getConfiguration();

			if (newConfiguration != null) {
				newConfiguration.putAll(configuration);
			} else {
				newConfiguration = configuration;
			}

			getServiceManager().setConfiguration(newConfiguration);
		}

		if (getLoggersManager() != null) {
			Properties newConfiguration = getLoggersManager().getConfiguration();

			if (newConfiguration != null) {
				newConfiguration.putAll(configuration);
			} else {
				newConfiguration = configuration;
			}

			getLoggersManager().setConfiguration(newConfiguration);
		}

		initDone = true;

	}

	private void injectServletContext(Object instance) {
		// Inject servlet context if possible
		if (instance instanceof IServletContextAware && servletContextProvider != null) {
			((IServletContextAware) instance).setServletContext(servletContextProvider.getServletContext());
		}
	}

	/**
	 * Load plugins from classpath.
	 * <p>
	 * If a manager has already been set with Spring, it is NOT overriden by
	 * plugins found in classpath.
	 */
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
				if (batchManagerClass != null && batchManager == null) {
					batchManager = (IBatchManager) getClassInstance(batchManagerClass);
				}

				// serviceManager
				String serviceManagerClass = p.getProperty("serviceManager");
				if (serviceManagerClass != null && serviceManager == null) {
					serviceManager = (IServiceManager) getClassInstance(serviceManagerClass);
				}

				// loggersManager
				String loggersManagerClass = p.getProperty("loggersManager");
				if (loggersManagerClass != null && loggersManager == null) {
					loggersManager = (ILoggersManager) getClassInstance(loggersManagerClass);
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

	public void setBatchManager(IBatchManager batchManager) {
		this.batchManager = batchManager;
	}

	/**
	 * @param batchScheduleManager
	 *            the batchScheduleManager to set
	 */
	public void setBatchScheduleManager(IBatchScheduleManager batchScheduleManager) {
		this.batchScheduleManager = batchScheduleManager;
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

	public void setServiceManager(IServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	public void setServletContextProvider(IServletContextProvider servletContext) {
		this.servletContextProvider = servletContext;
	}
}
