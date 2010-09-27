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

import static net.sf.appstatus.IStatusResult.ERROR;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import net.sf.appstatus.annotations.AppCheckMethod;
import net.sf.appstatus.check.impl.StatusResultImpl;
import net.sf.appstatus.core.IObjectInstantiationListener;
import net.sf.appstatus.util.Tuple;

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
  private final ArrayList<Tuple<Object, Method>> probes;
  private final List<IPropertyProvider> propertyProviders;
  private IServletContextProvider servletContextProvider = null;

  /**
   * Status Service creator.
   */
  public StatusService() {

    probes = new ArrayList<Tuple<Object, Method>>();
    propertyProviders = new ArrayList<IPropertyProvider>();

  }

  public List<IStatusResult> checkAll() {

    ArrayList<IStatusResult> l = new ArrayList<IStatusResult>();

    for (Tuple<Object, Method> check : probes) {

      Object obj = check.getFirst();
      Method meth = check.getSecond();

      IStatusResult result;
      try {
        result = (IStatusResult) meth.invoke(obj, (Object[]) null);
      } catch (Exception e) {
        String name = meth.getName();
        String className = meth.getClass().getCanonicalName();
        String message = "Error " + e.getMessage() + " when calling " + name;
        result = new StatusResultImpl(ERROR, message, false, className, "see log files");

        logger.error("calling check method " + name + " in " + className + " produced an error", e);
      }
      l.add(result);

    }
    return l;

  }

  private Object getInstance(String className) throws InstantiationException, IllegalAccessException,
      ClassNotFoundException {
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

  /**
   * TODO document this.
   * 
   * TODO enlever la contrainte sur le nom de clef ("check" et "property")
   * scanner toutes les classes du fichier properties
   */
  public void init() {
    try {
      // Load and init all probes
      Enumeration<URL> configFiles;

      configFiles = StatusService.class.getClassLoader().getResources(CONFIG_LOCATION);

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
            Object checker = getInstance(clazz);
            if (checker instanceof IServletContextAware) {
              ((IServletContextAware) checker).setServletContext(servletContextProvider.getServletContext());
            }

            Method[] allMethods = checker.getClass().getMethods();
            for (Method method : allMethods) {
              if (isACheckMethod(method)) {
                probes.add(new Tuple<Object, Method>(checker, method));
              }
            }

            logger.info("Registered status checker " + clazz);
          } else if (name.startsWith("property")) {
            String clazz = (String) p.get(name);
            IPropertyProvider provider = (IPropertyProvider) getInstance(clazz);
            propertyProviders.add(provider);
            if (provider instanceof IServletContextAware && servletContextProvider != null) {
              ((IServletContextAware) provider).setServletContext(servletContextProvider.getServletContext());
            }
            // else : guess is if we don't have the servlet context now, we will
            // later.
            logger.info("Registered property provider " + clazz);
          }
        }
      }
    } catch (Exception e) {
      logger.error("Initialization error", e);
    }

  }

  /**
   * Verify if a given method is a check method.
   * 
   * <p>
   * The signature MUST match : <code>public IStatusResult doSomething() </code>
   * OR <code>public static IStatusResult doSomething() </code>
   * </p>
   * 
   * @param method
   *          the method to test.
   * @return true is the given method is pulic, returns an {@link IStatusResult}
   *         , does not have any parameter and is annotated by
   *         {@link AppCheckMethod}
   */
  private boolean isACheckMethod(Method method) {
    return Modifier.isPublic(method.getModifiers()) && method.getReturnType().isAssignableFrom(IStatusResult.class)
        && (method.getParameterTypes().length == 0) && method.isAnnotationPresent(AppCheckMethod.class);
  }

  public void setObjectInstanciationListener(IObjectInstantiationListener objectInstanciationListener) {
    this.objectInstanciationListener = objectInstanciationListener;
  }

  public void setServletContextProvider(IServletContextProvider servletContext) {
    this.servletContextProvider = servletContext;
  }
}
