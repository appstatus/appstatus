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
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import net.sf.appstatus.core.services.IService;
import net.sf.appstatus.core.services.IServiceManager;
import net.sf.appstatus.core.services.IServiceMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the entry point for services monitor.
 * 
 * <p>
 * Must be initialized once before calling other methods.
 * <p>
 * AppStatusServices services = new AppStatusServices(); <br/>
 * services.init();
 * </p>
 * 
 * @author Nicolas Richeton
 * 
 */
@Deprecated
public class AppStatusServices {

	private static Logger logger = LoggerFactory
			.getLogger(AppStatusServices.class);

	private boolean initDone = false;
	private IObjectInstantiationListener objectInstanciationListener = null;
	private IServiceManager serviceManager = null;
	private IServletContextProvider servletContextProvider = null;

	/**
	 * Status Service creator.
	 */
	public AppStatusServices() {
	}

	private void checkInit() {
		if (!initDone) {
			logger.warn("Not initialized. Starting init");
			init();

		}
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
				obj = Class.forName(className).newInstance();
			} catch (ClassNotFoundException e) {
				logger.warn("Class {} not found ", className, e);
			} catch (InstantiationException e) {
				logger.warn("Cannot instanciate {} ", className, e);
			} catch (IllegalAccessException e) {
				logger.warn("Cannot access class {} for instantiation ",
						className, e);
			}
		}

		if (obj != null) {
			injectServletContext(obj);
		}

		return obj;
	}

	public IServiceMonitor getServiceMonitor(String name, String group) {
		checkInit();

		IService batch = null;
		if (serviceManager != null) {
			batch = serviceManager.getService(name, group);
			return serviceManager.getMonitor(batch);
		}

		return null;
	}

	public List<IService> getServices() {
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

		try {

			// Load plugins
			loadPlugins();
		} catch (Exception e) {
			logger.error("Initialization error", e);
		}

		initDone = true;

	}

	private void injectServletContext(Object instance) {
		// Inject servlet context if possible
		if (instance instanceof IServletContextAware
				&& servletContextProvider != null) {
			((IServletContextAware) instance)
					.setServletContext(servletContextProvider
							.getServletContext());
		}
	}

	private void loadPlugins() {
		try {
			Enumeration<URL> plugins = AppStatusServices.class.getClassLoader()
					.getResources("net/sf/appstatus/plugin.properties");
			while (plugins.hasMoreElements()) {
				URL url = plugins.nextElement();
				logger.info(url.toString());
				Properties p = loadProperties(url);

				// serviceManager
				String serviceManagerClass = p.getProperty("serviceManager");
				if (serviceManagerClass != null) {
					serviceManager = (IServiceManager) getClassInstance(serviceManagerClass);
				}

			}
		} catch (IOException e) {
			logger.warn("Error loading plugins", e);
		}
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

	public void setObjectInstanciationListener(
			IObjectInstantiationListener objectInstanciationListener) {
		this.objectInstanciationListener = objectInstanciationListener;
	}

	public void setServletContextProvider(IServletContextProvider servletContext) {
		this.servletContextProvider = servletContext;
	}
}
