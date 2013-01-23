/*
 * Copyright 2010 Capgemini
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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.AppStatusStatic;
import net.sf.appstatus.core.IServletContextProvider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusServlet extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(AppStatus.class);
	private static final long serialVersionUID = 3912325072098291029L;
	private static StatusWebHandler statusWeb = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		statusWeb.doGet(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		statusWeb.doPost(req, resp);
	}

	/**
	 * Init the AppStatus Web UI.
	 * <p>
	 * Read <b>bean</> init parameter. If defined, switch to Spring-enabled
	 * behavior.
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		super.init();

		String beanName = getInitParameter("bean");

		AppStatus status;
		if (beanName != null) {
			status = (AppStatus) (new SpringObjectInstantiationListener(this.getServletContext()).getInstance(beanName));
		} else {
			status = AppStatusStatic.getInstance();
		}

		status.setServletContextProvider(new IServletContextProvider() {
			public ServletContext getServletContext() {
				return StatusServlet.this.getServletContext();
			}
		});

		statusWeb = new StatusWebHandler();
		statusWeb.setAppStatus(status);
		statusWeb.setApplicationName(StringUtils.defaultString(config.getServletContext().getServletContextName(),
				"No name"));
		statusWeb.init();
	}
}
