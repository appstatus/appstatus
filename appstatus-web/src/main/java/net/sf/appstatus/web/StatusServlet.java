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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.AppStatusStatic;
import net.sf.appstatus.core.IServletContextProvider;
import net.sf.appstatus.web.pages.AbstractPage;
import net.sf.appstatus.web.pages.BatchPage;
import net.sf.appstatus.web.pages.Icons;
import net.sf.appstatus.web.pages.ServicesPage;
import net.sf.appstatus.web.pages.StatusPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusServlet extends HttpServlet {

	private static final String ENCODING = "UTF-8";
	private static Logger logger = LoggerFactory.getLogger(AppStatus.class);
	private static final long serialVersionUID = 3912325072098291029L;
	private static AppStatus status = null;
	private static AppStatus statusBatches = null;
	private static AppStatus statusServices = null;
	private String allow = null;
	private final Map<String, AbstractPage> pages = new HashMap<String, AbstractPage>();

	private final String styleSheet = "<style type=\"text/css\" media=\"screen\">"
			+ "table { font-size: 80%; }"
			+ "table ,th, td {  border: 1px solid black; border-collapse:collapse;}"
			+ "th { background-color: #DDDDDD; }" + "</style>";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (allow != null) {
			if (!req.getRemoteAddr().equals(allow)) {
				resp.sendError(401, "IP not authorized");
				return;
			}
		}

		if (req.getParameter("icon") != null) {
			Icons.render(resp.getOutputStream(), req.getParameter("icon"));
			return;
		}

		if (req.getParameter("p") != null
				&& pages.containsKey(req.getParameter("p"))) {
			pages.get(req.getParameter("p")).doGet(status, req, resp);

		} else {
			pages.get("status").doGet(status, req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (allow != null) {
			if (!req.getRemoteAddr().equals(allow)) {
				resp.sendError(401, "IP not authorized");
				return;
			}
		}

		if (req.getParameter("p") != null
				&& pages.containsKey(req.getParameter("p"))) {
			pages.get(req.getParameter("p")).doPost(status, req, resp);

		} else {
			pages.get("status").doPost(status, req, resp);
		}

		doGet(req, resp);
	}

	@Override
	public void init() throws ServletException {
		super.init();

		pages.put("batch", new BatchPage());
		pages.put("services", new ServicesPage());
		pages.put("status", new StatusPage());

		String beanName = null;
		try {
			beanName = getInitParameter("bean");

			InputStream is = StatusServlet.class
					.getResourceAsStream("/status-web-conf.properties");

			if (is == null) {
				logger.warn("/status-web-conf.properties not found in classpath. Using default configuration");
			} else {
				Properties p = new Properties();
				p.load(is);
				is.close();
				allow = (String) p.get("ip.allow");
			}
		} catch (Exception e) {
			logger.error(
					"Error loading configuration from /status-web-conf.properties.",
					e);
		}

		if (beanName != null) {
			status = (AppStatus) (new SpringObjectInstantiationListener(
					this.getServletContext()).getInstance(beanName));
		} else {
			status = AppStatusStatic.getInstance();
		}

		status.setServletContextProvider(new IServletContextProvider() {
			public ServletContext getServletContext() {
				return StatusServlet.this.getServletContext();
			}
		});
		status.init();

		statusServices = status;
		statusBatches = status;
	}
}
