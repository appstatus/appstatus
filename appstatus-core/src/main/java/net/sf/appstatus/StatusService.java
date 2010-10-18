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

import static net.sf.appstatus.check.impl.StatusResult.ERROR;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import net.sf.appstatus.annotations.AppCheckMethod;
import net.sf.appstatus.annotations.AppStatusProperties;
import net.sf.appstatus.check.impl.StatusResult;
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
  private IServletContextProvider servletContextProvider = null;

  final ArrayList<Tuple<Object, Method>> probes;
  final ArrayList<Tuple<Object, Method>> propertiesProviders;

  /**
   * Status Service creator.
   */
  public StatusService() {
    probes = new ArrayList<Tuple<Object, Method>>();
    propertiesProviders = new ArrayList<Tuple<Object, Method>>();
  }

  private void addChecker(Object checker) {
    Method[] allMethods = checker.getClass().getMethods();
    for (Method method : allMethods) {
      if (isACheckMethod(method)) {
        probes.add(new Tuple<Object, Method>(checker, method));
        logger.info("Registered status checker {} in {} ", method.getName(), checker.getClass().getName());
      }
    }
  }

  private void addPropertiesProvider(Object provider) {
    Method[] allMethods = provider.getClass().getMethods();
    for (Method method : allMethods) {
      if (isAPropertiesMethod(method)) {
        propertiesProviders.add(new Tuple<Object, Method>(provider, method));
        logger.info("Registered property provider {} in {} ", method.getName(), provider.getClass().getName());
      }
    }

  }

  public List<StatusResult> checkAll() {

    ArrayList<StatusResult> l = new ArrayList<StatusResult>();

    for (Tuple<Object, Method> check : probes) {

      Object obj = check.getFirst();
      Method meth = check.getSecond();

      StatusResult result;
      try {
        long start = System.currentTimeMillis();
        result = (StatusResult) meth.invoke(obj, (Object[]) null);
        System.err.println("invocation dur�e" + (System.currentTimeMillis() - start));
      } catch (Exception e) {
        String name = meth.getName();
        String className = meth.getClass().getCanonicalName();
        String message = "Error " + e.getMessage() + " when calling " + name;
        result = new StatusResult(ERROR, className, message, "see log files");

        logger.error("calling check method {} in {} produced an error", new Object[] { name, className, e });
      }
      l.add(result);

    }
    return l;

  }

  /**
   * Instanciate a class if possible, return null otherwise.
   * 
   * @param className
   *          the class name
   * @return return an instance of "className" class, return null if an error
   *         occurs of class not found
   */
  private Object getInstance(String className) {
    if (objectInstanciationListener != null) {
      Object obj = objectInstanciationListener.getInstance(className);
      if (obj != null) {
        return obj;
      }
    }

    try {
      Class<?> clazz = Class.forName(className);
      return clazz.newInstance();
    } catch (ClassNotFoundException e) {
      logger.error("Cannot find class {} ", className, e);
      return null;
    } catch (InstantiationException e) {
      logger.error("Cannot instantiate class {} ", className, e);
      return null;
    } catch (IllegalAccessException e) {
      logger.error("Cannot access class {} ", className, e);
      return null;
    }
  }

  public IObjectInstantiationListener getObjectInstanciationListener() {
    return objectInstanciationListener;
  }

  public Map<String, Map<String, String>> getProperties() {
    TreeMap<String, Map<String, String>> categories = new TreeMap<String, Map<String, String>>();

    for (Tuple<Object, Method> tuple : propertiesProviders) {
      Method m = tuple.getSecond();
      Object target = tuple.getFirst();

      AppStatusProperties annot = m.getAnnotation(AppStatusProperties.class);
      String category = annot.value();
      Map<String, String> properties = null;

      try {
        properties = (Map<String, String>) m.invoke(target);
      } catch (IllegalArgumentException e) {
        properties = Collections.singletonMap("invocation error", e.getMessage());
      } catch (IllegalAccessException e) {
        properties = Collections.singletonMap("invocation error", e.getMessage());
      } catch (InvocationTargetException e) {
        properties = Collections.singletonMap("invocation error", e.getMessage());
      } catch (ClassCastException e) {
        // TODO: handle exception
      }

      Map<String, String> oldProperties = categories.get(category);
      if (oldProperties == null) {
        oldProperties = new TreeMap<String, String>();
        categories.put(category, oldProperties);
      }

      oldProperties.putAll(properties);
    }

    return categories;
  }

  public IServletContextProvider getServletContext() {
    return servletContextProvider;
  }

  /**
   * TODO document this.
   * 
   * TODO remove all tests on key name ("check" et "property"), scan all classes
   * of properties file
   */
  public void init() {
    try {
      // Load and init all probes
      Enumeration<URL> configFiles;

      configFiles = StatusService.class.getClassLoader().getResources(CONFIG_LOCATION);

      if (configFiles == null) {
        logger.warn("No config file found");
        return;
      }

      while (configFiles.hasMoreElements()) {
        URL url = configFiles.nextElement();

        Properties p = loadProperties(url);

        Set<String> keys = p.stringPropertyNames();
        for (String name : keys) {
          String clazz = (String) p.get(name);

          Object provider = getInstance(clazz);

          if (provider == null) {
            logger.warn("Class not found {} ", clazz);
            continue;
          }
          if (provider instanceof IServletContextAware && servletContextProvider != null) {
            ((IServletContextAware) provider).setServletContext(servletContextProvider.getServletContext());
          }
          // else : guess is if we don't have the servlet context now, we will
          // later.

          // is there any checker methods
          addChecker(provider);
          // is there any properties provider methods
          addPropertiesProvider(provider);
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
   * The signature MUST match : <code>public StatusResult doSomething() </code>
   * OR <code>public static StatusResult doSomething() </code>
   * </p>
   * 
   * @param method
   *          the method to test.
   * @return true is the given method is pulic, returns an {@link StatusResult}
   *         , does not have any parameter and is annotated by
   *         {@link AppCheckMethod}
   */
  protected boolean isACheckMethod(Method method) {
    return Modifier.isPublic(method.getModifiers()) && method.getReturnType().isAssignableFrom(StatusResult.class)
        && (method.getParameterTypes().length == 0) && method.isAnnotationPresent(AppCheckMethod.class);
  }

  private boolean isAPropertiesMethod(Method method) {
    // FIXME method.getReturnType().isAssignableFrom(HashMap.class) cannot test
    // generic types
    // should use method.getGenericReturnType()
    // Type t = method.getGenericReturnType();
    // if (t instanceof ParameterizedType) {
    //
    // }
    // then match java/util/Map as rawtype and
    // java/lang/String as actual type arguments

    return Modifier.isPublic(method.getModifiers()) && method.getReturnType().isAssignableFrom(Map.class)
        && (method.getParameterTypes().length == 0) && method.isAnnotationPresent(AppStatusProperties.class);
  }

  private Properties loadProperties(URL url) throws IOException {
    Properties p;
    InputStream is;
    // Load plugin configuration
    p = new Properties();
    is = url.openStream();
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
