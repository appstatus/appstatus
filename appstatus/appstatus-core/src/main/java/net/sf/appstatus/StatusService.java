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
  protected final List<IStatusChecker> probes;
  private final List<IPropertyProvider> propertyProviders;
  private IServletContextProvider servletContextProvider = null;

  /**
   * Status Service creator.
   */
  public StatusService() {
    probes = new ArrayList<IStatusChecker>();
    propertyProviders = new ArrayList<IPropertyProvider>();
  }

  private void addPropertyProvider(String clazz) {
    IPropertyProvider provider = (IPropertyProvider) getInstance(clazz);
    if (provider instanceof IServletContextAware && servletContextProvider != null) {
      ((IServletContextAware) provider).setServletContext(servletContextProvider.getServletContext());
    }
    // else : guess is if we don't have the servlet context now, we will
    // later.

    if (provider != null) {
      propertyProviders.add(provider);
      logger.info("Registered property provider " + clazz);
    } else {
      logger.error("cannot instanciate class {}, Please configure \"{}\" file properly", clazz, CONFIG_LOCATION);
    }
  }

  private void addStatusChecker(String clazz) {
    IStatusChecker check = (IStatusChecker) getInstance(clazz);
    if (check == null) {
      logger.error("cannot instanciate class {}, Please configure \"{}\" file properly", clazz, CONFIG_LOCATION);
      return;
    }

    if (check instanceof IServletContextAware) {
      ((IServletContextAware) check).setServletContext(servletContextProvider.getServletContext());
    }

    probes.add(check);
    logger.info("Registered status checker " + clazz);
  }

  public List<IStatusResult> checkAll() {
    ArrayList<IStatusResult> statusList = new ArrayList<IStatusResult>();
    for (IStatusChecker check : probes) {
      statusList.add(check.checkStatus());
    }
    return statusList;
  }

  /**
   * Try to instantiate a class.
   * 
   * @param className
   * @return an object instance of "className" class or null if instantiation is
   *         not possible
   */
  private Object getInstance(String className) {
    Object obj = null;

    if (objectInstanciationListener != null) {
      obj = objectInstanciationListener.getInstance(className);
    }

    if (obj != null) {
      return obj;
    }

    try {
      return Class.forName(className).newInstance();
    } catch (ClassNotFoundException e) {
      logger.warn("Class {} not found ", className, e);
    } catch (InstantiationException e) {
      logger.warn("Cannot instanciate {} ", className, e);
    } catch (IllegalAccessException e) {
      logger.warn("Cannot access class {} for instantiation ", className, e);
    }
    return null;
  }

  public IObjectInstantiationListener getObjectInstanciationListener() {
    return objectInstanciationListener;
  }

  public Map<String, Map<String, String>> getProperties() {
    TreeMap<String, Map<String, String>> categories = new TreeMap<String, Map<String, String>>();

    for (IPropertyProvider provider : propertyProviders) {
      if (categories.get(provider.getCategory()) == null) {
        categories.put(provider.getCategory(), new TreeMap<String, String>());
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

      configFiles = StatusService.class.getClassLoader().getResources(CONFIG_LOCATION);

      if (configFiles == null) {
        logger.info("config file {} not found in classpath", CONFIG_LOCATION);
        return;
      }

      while (configFiles.hasMoreElements()) {
        Properties p = loadProperties(configFiles.nextElement());

        Set<String> keys = p.stringPropertyNames();
        for (String name : keys) {
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

  /**
   * Load a properties file from a given URL.
   * 
   * @param url
   *          an url
   * @return a {@link Properties} object
   * @throws IOException
   *           in an error occurs
   */
  private Properties loadProperties(URL url) throws IOException {
    // Load plugin configuration
    Properties p = new Properties();
    InputStream is = url.openStream();
    p.load(is);
    is.close();
    return p;
  }

  public void setObjectInstanciationListener(IObjectInstantiationListener objectInstanciationListener) {
    this.objectInstanciationListener = objectInstanciationListener;
  }

  public void setServletContextProvider(IServletContextProvider servletContext) {
    this.servletContextProvider = servletContext;
  }
}
