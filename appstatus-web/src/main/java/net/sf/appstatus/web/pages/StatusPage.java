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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.check.ICheckResult;
import net.sf.appstatus.web.HtmlUtils;
import net.sf.appstatus.web.StatusWebHandler;

import org.apache.commons.lang3.text.StrBuilder;

public class StatusPage extends AbstractPage {
	private static final String ENCODING = "UTF-8";
	private static final String PAGECONTENTLAYOUT = "statusContentLayout.html";

	/**
	 * Returns status icon id.
	 * 
	 * @param result
	 * @return
	 */
	private static String getStatus(ICheckResult result) {

		if (result.getCode() == ICheckResult.OK) {
			return Resources.STATUS_OK;
		}

		if (result.isFatal()) {
			return Resources.STATUS_ERROR;
		}

		return Resources.STATUS_WARN;
	}

	@Override
	public void doGet(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException {

		if (req.getParameter("json") != null) {
			doGetJSON(webHandler, req, resp);
		} else {
			doGetHTML(webHandler, req, resp);
		}

	}

	public void doGetHTML(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException {

		setup(resp, "text/html");
		ServletOutputStream os = resp.getOutputStream();

		Map<String, String> valuesMap = new HashMap<String, String>();
		List<ICheckResult> results = webHandler.getAppStatus().checkAll();
		Collections.sort(results);
		boolean statusOk = true;
		int statusCode = 200;
		for (ICheckResult r : results) {
			if (r.getCode() != ICheckResult.OK && r.isFatal()) {
				resp.setStatus(500);
				statusCode = 500;
				statusOk = false;
				break;
			}
		}

		valuesMap.put("statusOk", String.valueOf(statusOk));
		valuesMap.put("statusCode", String.valueOf(statusCode));

		// STATUS TABLE
		StrBuilder sbStatusTable = new StrBuilder();
		if (HtmlUtils.generateBeginTable(sbStatusTable, results.size())) {

			HtmlUtils.generateHeaders(sbStatusTable, "", "Group", "Name", "Description", "Code", "Resolution");

			for (ICheckResult r : results) {
				HtmlUtils.generateRow(sbStatusTable, getStatus(r), r.getGroup(), r.getProbeName(), r.getDescription(),
						String.valueOf(r.getCode()), r.getResolutionSteps());
			}
			HtmlUtils.generateEndTable(sbStatusTable, results.size());
		}
		valuesMap.put("statusTable", sbStatusTable.toString());

		// PROPERTIES TABLE
		StrBuilder sbPropertiesTable = new StrBuilder();
		Map<String, Map<String, String>> properties = webHandler.getAppStatus().getProperties();
		if (HtmlUtils.generateBeginTable(sbPropertiesTable, properties.size())) {

			HtmlUtils.generateHeaders(sbPropertiesTable, "", "Group", "Name", "Value");

			for (Entry<String, Map<String, String>> cat : properties.entrySet()) {
				String category = cat.getKey();

				for (Entry<String, String> r : cat.getValue().entrySet()) {
					HtmlUtils.generateRow(sbPropertiesTable, Resources.STATUS_PROP, category, r.getKey(), r.getValue());
				}

			}
			HtmlUtils.generateEndTable(sbPropertiesTable, properties.size());
			valuesMap.put("propertiesTable", sbPropertiesTable.toString());
		}
		String content = HtmlUtils.applyLayout(valuesMap, PAGECONTENTLAYOUT);

		valuesMap.clear();
		valuesMap.put("content", content);
		os.write(getPage(webHandler, valuesMap).getBytes(ENCODING));
	}

	public void doGetJSON(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException {

		setup(resp, "application/json");
		ServletOutputStream os = resp.getOutputStream();
		int statusCode = 200;
		List<ICheckResult> results = webHandler.getAppStatus().checkAll();
		for (ICheckResult r : results) {
			if (r.isFatal()) {
				resp.setStatus(500);
				statusCode = 500;
				break;
			}
		}

		os.write("{".getBytes(ENCODING));

		os.write(("\"code\" : " + statusCode + ",").getBytes(ENCODING));
		os.write(("\"status\" : {").getBytes(ENCODING));

		boolean first = true;
		for (ICheckResult r : results) {
			if (!first) {
				os.write((",").getBytes(ENCODING));
			}

			os.write(("\"" + r.getProbeName() + "\" : " + r.getCode()).getBytes(ENCODING));

			if (first) {
				first = false;
			}
		}

		os.write("}".getBytes(ENCODING));
		os.write("}".getBytes(ENCODING));

	}

	@Override
	public void doPost(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp) {
	}

	@Override
	public String getId() {
		return "status";
	}

	@Override
	public String getName() {
		return "Status";
	}
}
