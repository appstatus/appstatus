/*
 * Copyright 2010 Capgemini and others. Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sf.appstatus.check.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import net.sf.appstatus.IServletContextAware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieve POM informations for WAR archive.
 * 
 * @author Nicolas Richeton (Capgemini)
 * 
 */
public class WarMavenVersionProvider extends AbstractPropertyProvider implements
		IServletContextAware {

	private static final String NOT_AVAILABLE = "Not available";
	private static final String ARTIFACT_ID = "artifactId";
	private static final String CATEGORY = "maven";
	private static final String GROUP_ID = "groupId";
	private static Logger logger = LoggerFactory
			.getLogger(WarMavenVersionProvider.class);
	private static final String VERSION = "version";
	private ServletContext servletContext = null;

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.sf.appstatus.IPropertyProvider#getCategory()
	 */
	public String getCategory() {
		return CATEGORY;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.sf.appstatus.IPropertyProvider#getProperties()
	 */
	public Map<String, String> getProperties() {
		InputStream url = null;
		Properties pomProperties = null;
		Map<String, String> prop = new HashMap<String, String>();
		if (this.servletContext != null) {
			try {
				url = servletContext.getResourceAsStream(servletContext
						.getResourcePaths(
								(String) servletContext
										.getResourcePaths("/META-INF/maven/")
										.iterator().next()).iterator().next()
						+ "pom.properties");
				if (url != null) {
					pomProperties = new Properties();
					pomProperties.load(url);
					url.close();
				}
			} catch (Exception e) {
				logger.warn(
						"Error getting maven information from /META-INF/maven/*",
						e);
			}
			if (pomProperties == null) {
				prop.put(VERSION, NOT_AVAILABLE);
				prop.put(GROUP_ID, NOT_AVAILABLE);
				prop.put(ARTIFACT_ID, NOT_AVAILABLE);
			} else {
				prop.put(VERSION, pomProperties.getProperty(VERSION));
				prop.put(GROUP_ID, pomProperties.getProperty(GROUP_ID));
				prop.put(ARTIFACT_ID, pomProperties.getProperty(ARTIFACT_ID));
			}
		}

		return prop;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see net.sf.appstatus.IServletContextAware#setServletContext(javax.servlet.ServletContext)
	 */
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
