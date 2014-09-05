/*
 * Copyright 2010-2013 Capgemini Licensed under the Apache License, Version 2.0 (the
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
package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.loggers.ILoggersManager;
import net.sf.appstatus.core.loggers.LoggerConfig;
import net.sf.appstatus.web.HtmlUtils;
import net.sf.appstatus.web.StatusWebHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Display loggers current level and let the user change them.
 * 
 * @author Romain Gonord
 * 
 */
public class LoggersPage extends AbstractPage {
	private static final String ENCODING = "UTF-8";
	private static final String LEVEL_ERROR = "ERROR";
	private static final String LEVEL_INFO = "INFO";
	private static final String LEVEL_TRACE = "TRACE";
	private static final String LEVEL_WARN = "WARN";
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggersPage.class);
	private static final String PAGECONTENTLAYOUT = "logContentLayout.html";

	@Override
	public void doGet(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException {
		LOGGER.debug("doGet");
		if (StringUtils.isNotBlank(req.getParameter("name")) && StringUtils.isNotBlank(req.getParameter("level"))) {
			LoggerConfig logger2Change = new LoggerConfig(req.getParameter("name"), req.getParameter("level"));
			LOGGER.debug("Change log level : {} - {}", logger2Change.getName(), logger2Change.getLevel());
			webHandler.getAppStatus().getLoggersManager().update(logger2Change);
		}
		setup(resp, "text/html");
		ServletOutputStream os = resp.getOutputStream();
		Map<String, String> valuesMap = new HashMap<String, String>();
		// build sbLoggersTable
		StrBuilder sbLoggersTable = new StrBuilder();
		List<LoggerConfig> loggers = webHandler.getAppStatus().getLoggersManager().getLoggers();
		if (HtmlUtils.generateBeginTable(sbLoggersTable, loggers.size())) {
			HtmlUtils.generateHeaders(sbLoggersTable, "", "Name", "Levels", "", "", "", "");
			for (LoggerConfig logger : loggers) {
				HtmlUtils.generateRow(sbLoggersTable, Resources.STATUS_PROP, logger.getName(),
						getButton(LEVEL_TRACE, logger), getButton(ILoggersManager.LEVEL_DEBUG, logger),
						getButton(LEVEL_INFO, logger), getButton(LEVEL_WARN, logger), getButton(LEVEL_ERROR, logger));
			}
			HtmlUtils.generateEndTable(sbLoggersTable, loggers.size());
		}
		// generating content
		valuesMap.put("loggersTable", sbLoggersTable.toString());
		valuesMap.put("loggerCount", String.valueOf(loggers.size()));
		String content = HtmlUtils.applyLayout(valuesMap, PAGECONTENTLAYOUT);
		valuesMap.clear();
		valuesMap.put("content", content);
		// generating page
		os.write(getPage(webHandler, valuesMap).getBytes(ENCODING));
	}

	@Override
	public void doPost(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp) {
		// nothing to do
	}

	private String getButton(String level, LoggerConfig logger) {
		String buttonTypeTmp = "";
		if (level.equals(logger.getLevel())) {
			if (LEVEL_TRACE.equals(level)) {
				buttonTypeTmp = "btn-info";
			} else if (ILoggersManager.LEVEL_DEBUG.equals(level)) {
				buttonTypeTmp = "btn-primary";
			} else if (LEVEL_INFO.equals(level)) {
				buttonTypeTmp = "btn-success";
			} else if (LEVEL_WARN.equals(level)) {
				buttonTypeTmp = "btn-warning";
			} else if (LEVEL_ERROR.equals(level)) {
				buttonTypeTmp = "btn-danger";
			}
		}
		return "<a class='btn btn-mini " + buttonTypeTmp + "' href='?p=loggers&level=" + level + "&name="
				+ logger.getName() + "'>" + level + "</a>";
	}

	@Override
	public String getId() {
		return "loggers";
	}

	@Override
	public String getName() {
		return "Loggers";
	}
}