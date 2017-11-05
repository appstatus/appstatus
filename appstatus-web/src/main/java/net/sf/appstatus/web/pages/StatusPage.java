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
package net.sf.appstatus.web.pages;

import static java.util.Collections.sort;
import static net.sf.appstatus.web.HtmlUtils.applyLayout;
import static net.sf.appstatus.web.HtmlUtils.generateBeginTable;
import static net.sf.appstatus.web.HtmlUtils.generateEndTable;
import static net.sf.appstatus.web.HtmlUtils.generateHeaders;
import static net.sf.appstatus.web.HtmlUtils.generateRow;
import static net.sf.appstatus.web.HtmlUtils.json;
import static org.apache.commons.lang3.StringUtils.join;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.check.ICheckResult;
import net.sf.appstatus.web.StatusWebHandler;

public class StatusPage extends AbstractPage {

	private static final String ENCODING = "UTF-8";

	private static Logger LOGGER = LoggerFactory.getLogger(StatusPage.class);

	private static final String PAGECONTENTLAYOUT = "statusContentLayout.html";

	/**
	 * Returns status icon id.
	 *
	 * @param result
	 * @return
	 */
	private static String getStatus(final ICheckResult result) {

		if (result.getCode() == ICheckResult.OK) {
			return Resources.STATUS_OK;
		}

		if (result.isFatal()) {
			return Resources.STATUS_ERROR;
		}

		return Resources.STATUS_WARN;
	}

	@Override
	public void doGet(final StatusWebHandler webHandler, final HttpServletRequest req, final HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException {

		if (req.getParameter("json") != null) {
			doGetJSON(webHandler, req, resp);
		} else {
			doGetHTML(webHandler, req, resp);
		}

	}

	public void doGetHTML(final StatusWebHandler webHandler, final HttpServletRequest req,
			final HttpServletResponse resp) throws UnsupportedEncodingException, IOException {

		setup(resp, "text/html");
		final ServletOutputStream os = resp.getOutputStream();

		final AppStatus appStatus = webHandler.getAppStatus();

		final Map<String, String> valuesMap = new HashMap<String, String>();
		final List<ICheckResult> results = appStatus.checkAll(req.getLocale());
		sort(results);
		boolean statusOk = true;
		int statusCode = 200;
		for (final ICheckResult r : results) {
			if (r.getCode() != ICheckResult.OK && r.isFatal()) {
				statusCode = 500;
				statusOk = false;
				break;
			}
		}

		if (statusOk && appStatus.isMaintenance()) {
			statusCode = 503;
		}

		valuesMap.put("maintenanceModeActive", appStatus.isMaintenance() ? "on" : "off");
		valuesMap.put("nextMode", appStatus.isMaintenance() ? "available" : "maintenance");

		valuesMap.put("statusOk", String.valueOf(statusOk));
		valuesMap.put("statusCode", String.valueOf(statusCode));

		resp.setStatus(statusCode);

		// STATUS TABLE
		final StrBuilder sbStatusTable = new StrBuilder();
		if (generateBeginTable(sbStatusTable, results.size())) {

			generateHeaders(sbStatusTable, "", "Group", "Name", "Description", "Code", "Resolution", "Reset ?");

			for (final ICheckResult r : results) {
				generateRow(sbStatusTable, getStatus(r), r.getGroup(), r.getProbeName(), r.getDescription(),
						String.valueOf(r.getCode()), r.getResolutionSteps(), getResetForm(r));
			}
			generateEndTable(sbStatusTable, results.size());
		}
		valuesMap.put("statusTable", sbStatusTable.toString());

		// PROPERTIES TABLE
		final StrBuilder sbPropertiesTable = new StrBuilder();
		final Map<String, Map<String, String>> properties = appStatus.getProperties();
		if (generateBeginTable(sbPropertiesTable, properties.size())) {

			generateHeaders(sbPropertiesTable, "", "Group", "Name", "Value");

			for (final Entry<String, Map<String, String>> cat : properties.entrySet()) {
				final String category = cat.getKey();

				for (final Entry<String, String> r : cat.getValue().entrySet()) {
					generateRow(sbPropertiesTable, Resources.STATUS_PROP, category, r.getKey(), r.getValue());
				}

			}
			generateEndTable(sbPropertiesTable, properties.size());
		}
		valuesMap.put("propertiesTable", sbPropertiesTable.toString());
		final String content = applyLayout(valuesMap, PAGECONTENTLAYOUT);

		valuesMap.clear();
		valuesMap.put("content", content);
		os.write(getPage(webHandler, valuesMap).getBytes(ENCODING));
	}

	public void doGetJSON(final StatusWebHandler webHandler, final HttpServletRequest req,
			final HttpServletResponse resp) throws UnsupportedEncodingException, IOException {

		setup(resp, "application/json");

		final AppStatus appStatus = webHandler.getAppStatus();

		final ServletOutputStream os = resp.getOutputStream();
		int statusCode = 200;
		final List<ICheckResult> results = appStatus.checkAll(req.getLocale());
		for (final ICheckResult r : results) {
			if (r.isFatal()) {
				resp.setStatus(500);
				statusCode = 500;
				break;
			}
		}

		if (appStatus.isMaintenance()) {
			resp.setStatus(503);
			statusCode = 503;
		}

		os.write("{".getBytes(ENCODING));

		os.write((json("code", statusCode) + ",").getBytes(ENCODING));
		os.write(("\"status\" : [").getBytes(ENCODING));

		boolean first = true;
		for (final ICheckResult r : results) {
			if (!first) {
				os.write((",").getBytes(ENCODING));
			}

			os.write(("{" + join(new String[] { json("name", r.getProbeName()), json("group", r.getGroup()),
					json("description", r.getDescription()), json("resolution", r.getResolutionSteps()),
					json("code", r.getCode()) }, ",") + "}").getBytes(ENCODING));

			if (first) {
				first = false;
			}
		}

		os.write("]".getBytes(ENCODING));

		final Map<String, Map<String, String>> properties = appStatus.getProperties();

		os.write((", \"properties\" : [").getBytes(ENCODING));

		first = true;
		for (Entry<String, Map<String, String>> cat : properties.entrySet()) {

			for (final Entry<String, String> r : cat.getValue().entrySet()) {
				if (!first) {
					os.write((",").getBytes(ENCODING));
				}

				os.write(("{" + join(new String[] { json("group", cat.getKey()), json("name", r.getKey()),
						json("value", r.getValue()) }, ",") + "}").getBytes(ENCODING));

				if (first) {
					first = false;
				}
			}
		}

		os.write("]".getBytes(ENCODING));

		os.write("}".getBytes(ENCODING));

	}

	@Override
	public void doPost(final StatusWebHandler webHandler, final HttpServletRequest req,
			final HttpServletResponse resp) {
		updateMode(webHandler, req.getParameter("mode"));
		resetCheck(webHandler, req.getParameter("resetCheckerId"));
		try {
			resp.sendRedirect(req.getRequestURI());
		} catch (final IOException ex) {
			LOGGER.warn("Unable to redirect to main status page after updating mode", ex);
		}
	}

	@Override
	public String getId() {
		return "status";
	}

	@Override
	public String getName() {
		return "Status";
	}

	private String getResetForm(ICheckResult checkResult) {
		if (checkResult.isResettable()) {
			return "<form name=\"mode\" action=\"?resetCheckerId=" + checkResult.getCheckerId() + "\" method=\"post\">"
					+ "<button type=\"submit\" class=\"btn btn-mini btn-primary\">reset</button></form>";
		}

		return "-";
	}

	private void resetCheck(final StatusWebHandler webHandler, final String checkerId) {
		if (StringUtils.isBlank(checkerId)) {
			return;
		}

		webHandler.getAppStatus().resetCheck(checkerId);
	}

	private void updateMode(final StatusWebHandler webHandler, final String mode) {
		if (null != mode) {
			try {
				webHandler.getAppStatus().setMaintenance("maintenance".equalsIgnoreCase(mode));
			} catch (final IOException ex) {
				throw new RuntimeException("Error while updating maintenance status", ex);
			}
		}
	}
}
