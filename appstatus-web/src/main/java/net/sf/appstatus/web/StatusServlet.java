/*
 * Copyright 2010-2012 Capgemini
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.sf.appstatus.web;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.AppStatusStatic;
import net.sf.appstatus.core.IServletContextProvider;
import net.sf.appstatus.web.pages.BatchPage;
import net.sf.appstatus.web.pages.LoggersPage;
import net.sf.appstatus.web.pages.RadiatorPage;
import net.sf.appstatus.web.pages.ServicesPage;
import net.sf.appstatus.web.pages.StatusPage;

public class StatusServlet extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(AppStatus.class);
	private static final long serialVersionUID = 3912325072098291029L;
	private static StatusWebHandler statusWeb = null;

	private void addPage(final Map<String, IPage> map, final IPage page) {
		map.put(page.getId(), page);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.
	 * HttpServletRequest , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		statusWeb.doGet(req, resp);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.
	 * HttpServletRequest , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		statusWeb.doPost(req, resp);
	}

	/**
	 * Init the AppStatus Web UI.
	 * <p>
	 * Read <b>bean</> init parameter. If defined, switch to Spring-enabled
	 * behavior.
	 */
	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);

		super.init();

		SpringObjectInstantiationListener instantiation = null;
		final String beanName = getInitParameter("bean");
		final String[] pagesBeanNames = StringUtils.split(getInitParameter("custom-pages"), ", ");
		final Map<String, IPage> pages = new LinkedHashMap<String, IPage>();
		;

		AppStatus status;
		if (beanName != null) {
			// Using Spring
			instantiation = new SpringObjectInstantiationListener(this.getServletContext());

			// Status
			status = (AppStatus) instantiation.getInstance(beanName);

		} else {
			status = AppStatusStatic.getInstance();

		}

		status.setServletContextProvider(new IServletContextProvider() {
			public ServletContext getServletContext() {
				return StatusServlet.this.getServletContext();
			}
		});

		//
		// Pages
		addPage(pages, new StatusPage());

		if (status.getServiceManager() != null) {
			addPage(pages, new ServicesPage());
		}

		if (status.getBatchManager() != null) {
			addPage(pages, new BatchPage());

		}

		if (status.getLoggersManager() != null) {
			addPage(pages, new LoggersPage());

		}

		// Custom pages
		if (pagesBeanNames != null) {
			for (final String pageBean : pagesBeanNames) {

				IPage newPage = null;

				if (instantiation != null) {
					newPage = (IPage) instantiation.getInstance(pageBean);
					if (newPage == null) {
						try {
							newPage = (IPage) Thread.currentThread().getContextClassLoader().loadClass(pageBean)
									.newInstance();
						} catch (final ClassNotFoundException e) {
							logger.warn("Class {} not found ", pageBean, e);
						} catch (final InstantiationException e) {
							logger.warn("Cannot instanciate {} ", pageBean, e);
						} catch (final IllegalAccessException e) {
							logger.warn("Cannot access class {} for instantiation ", pageBean, e);
						}
					}
				}

				addPage(pages, newPage);

			}
		}

		// Radiator at the end.
		addPage(pages, new RadiatorPage());

		// Init
		statusWeb = new StatusWebHandler();
		statusWeb.setAppStatus(status);
		statusWeb.setApplicationName(
				StringUtils.defaultString(config.getServletContext().getServletContextName(), "No name"));
		statusWeb.setPages(pages);
		statusWeb.init();
	}
}
