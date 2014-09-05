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
package net.sf.appstatus.jmx;

import net.sf.appstatus.core.IObjectInstantiationListener;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * Simple Spring bean instance finder.
 * 
 * @author LABEMONT
 * 
 */
public class SpringBeanInstantiationListener implements IObjectInstantiationListener {

  private final ApplicationContext applicationContext;

  /**
   * Constructor.
   * 
   * @param springApplicationContext
   *          classique spring application context.
   */
  public SpringBeanInstantiationListener(ApplicationContext springApplicationContext) {
    this.applicationContext = springApplicationContext;
  }

  public Object getInstance(String className) {
    Object obj = null;

    try {
      obj = this.applicationContext.getBean(className);
    } catch (BeansException e) {
    }

    return obj;
  }
}
