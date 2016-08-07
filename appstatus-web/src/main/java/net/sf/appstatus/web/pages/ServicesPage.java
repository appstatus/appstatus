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

import static java.lang.Math.round;
import static java.util.Collections.sort;
import static net.sf.appstatus.web.HtmlUtils.applyLayout;
import static net.sf.appstatus.web.HtmlUtils.generateBeginTable;
import static net.sf.appstatus.web.HtmlUtils.generateEndTable;
import static net.sf.appstatus.web.HtmlUtils.generateHeaders;
import static net.sf.appstatus.web.HtmlUtils.generateRow;
import static net.sf.appstatus.web.HtmlUtils.json;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;
import static org.apache.commons.lang3.StringUtils.join;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.text.StrBuilder;

import net.sf.appstatus.core.services.IService;
import net.sf.appstatus.web.StatusWebHandler;

public class ServicesPage extends AbstractPage {
	private static final String ENCODING = "UTF-8";
	private static final String PAGECONTENTLAYOUT = "servicesContentLayout.html";

	public ServicesPage() {
		Resources.addResource("sparkline.js", "/assets/js/jquery.sparkline.min.js", "application/javascript");
		Resources.addResource("services.js", "/assets/js/services.js", "application/javascript");
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

	public void doGetHTML(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException {

		setup(resp, "text/html");
		ServletOutputStream os = resp.getOutputStream();

		Map<String, String> valuesMap = new HashMap<String, String>();

		List<IService> services = webHandler.getAppStatus().getServices();
		sort(services);

		StrBuilder sbServicesTable = new StrBuilder();

		if (generateBeginTable(sbServicesTable, services.size())) {

			generateHeaders(sbServicesTable, "", "Group", "Name", "Hits", "Cache", "Running", "min", "max", "avg",
					"nested", "min (c)", "max (c)", "avg (c)", "nested (c)", "Status", "Hit rate");

			for (IService service : services) {
				generateRow(sbServicesTable, Resources.STATUS_JOB, escapeHtml4(service.getGroup()),
						escapeHtml4(service.getName()), service.getHits(),
						getCountAndPercent("pie-cache",
								new Long[] { service.getCacheHits(), service.getHits() - service.getCacheHits() }),
						service.getRunning(), service.getMinResponseTime(), service.getMaxResponseTime(),
						round(service.getAvgResponseTime()), round(service.getAvgNestedCalls()),
						service.getMinResponseTimeWithCache(), service.getMaxResponseTimeWithCache(),
						round(service.getAvgResponseTimeWithCache()), round(service.getAvgNestedCallsWithCache()),
						getCountAndPercent("pie-status",
								new Long[] { service.getHits() - service.getErrors() - service.getFailures(),
										service.getFailures(), service.getErrors() }),
						"<span class='graph-rate' values=\"\"></span>");
			}

			generateEndTable(sbServicesTable, services.size());
		}

		// generating content
		valuesMap.put("servicesTable", sbServicesTable.toString());
		String content = applyLayout(valuesMap, PAGECONTENTLAYOUT);

		valuesMap.clear();
		valuesMap.put("content", content);
		valuesMap.put("js", "<script type='text/javascript' src='?resource=services.js'></script>");
		// generating page
		os.write(getPage(webHandler, valuesMap).getBytes(ENCODING));
	}

	private void doGetJSON(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException {

		List<IService> services = webHandler.getAppStatus().getServices();
		sort(services);

		final ServletOutputStream os = resp.getOutputStream();
		os.write("[".getBytes(ENCODING));

		boolean first = true;
		for (IService service : services) {
			if (!first) {
				os.write((",").getBytes(ENCODING));
			}

			os.write(("{ " + join(new String[] { json("group", service.getGroup()), json("name", service.getName()),
					json("hits", service.getHits()), json("cacheHits", service.getCacheHits()),
					json("running", service.getRunning()), json("minResponseTime", service.getMinResponseTime()),
					json("maxResponseTime", service.getMaxResponseTime()),
					json("avgResponseTime", service.getAvgResponseTime()),
					json("avgNestedCalls", service.getAvgNestedCalls()),
					json("minResponseTimeWithCache", service.getMinResponseTimeWithCache()),
					json("rate", service.getCurrentRate()) }, ", ") + "}").getBytes(ENCODING));

			if (first) {
				first = false;
			}
		}

		os.write("]".getBytes(ENCODING));

	}

	@Override
	public void doPost(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp) {

	}

	private String getCountAndPercent(String cssClass, Long[] values) {
		return "<span class='" + cssClass + "' values='" + join(values, ",") + "' ></span>";
	}

	@Override
	public String getId() {
		return "services";
	}

	@Override
	public String getName() {
		return "Services";
	}

	/**
	 * Returns a percentage string (including '%'), build with value1/value2.
	 *
	 * @param value1
	 * @param value2
	 * @return
	 */
	private String getPercent(long value1, long value2) {
		if (value2 == 0) {
			return "(-%)";
		}
		return " (" + ((100 * value1) / value2) + "%)";
	}

	private String getRate(double value1) {
		double rate = Math.round(value1 * 100) / 100d;

		return rate + " hits/s";
	}
}
