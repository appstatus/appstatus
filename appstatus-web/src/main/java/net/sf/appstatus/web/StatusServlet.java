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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.IServletContextProvider;
import net.sf.appstatus.IStatusResult;
import net.sf.appstatus.StatusService;
import net.sf.appstatus.monitor.resource.IStatusResource;
import net.sf.appstatus.monitor.resource.ResourceType;
import net.sf.appstatus.monitor.resource.batch.IStatusJobExecutionResource;
import net.sf.appstatus.monitor.resource.batch.IStatusJobResource;
import net.sf.appstatus.monitor.resource.service.IStatusServiceResource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusServlet extends HttpServlet {

	private static final String ENCODING = "UTF-8";
	private static Logger logger = LoggerFactory.getLogger(StatusService.class);
	private static final long serialVersionUID = 3912325072098291029L;
	private static StatusService status = null;
	private static final String STATUS_ERROR = "error";
	private static final String STATUS_OK = "ok";
	private static final String STATUS_PROP = "prop";
	private static final String STATUS_WARN = "warn";
	private String allow = null;
	private final String styleSheet = "<style type=\"text/css\" media=\"screen\">"
			+ "table { font-size: 80%; }"
			+ "table ,th, td {  border: 1px solid black; border-collapse:collapse;}"
			+ "th { background-color: #DDDDDD; }" + "</style>";
	private boolean useSpring = false;

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
			doGetResource(req.getParameter("icon"), req, resp);
			return;
		}

		boolean statusOk = true;
		int statusCode = 200;
		if (status.getStatus() == IStatusResult.ERROR) {
			resp.setStatus(500);
			statusCode = 500;
			statusOk = false;
		}

		resp.setContentType("text/html");
		resp.setCharacterEncoding(ENCODING);

		ServletOutputStream os = resp.getOutputStream();

		os.write("<html><head".getBytes(ENCODING));
		os.write(styleSheet.getBytes(ENCODING));
		os.write("<body>".getBytes(ENCODING));
		os.write("<h1>Status Page</h1>".getBytes(ENCODING));
		os.write(("<p>Online:" + statusOk + "</p>").getBytes(ENCODING));
		os.write(("<p>Code:" + statusCode + "</p>").getBytes(ENCODING));

		generateConfiguration(os);

		generateResourcesStatus(os);

		generateServiceResourcesStatus(os);

		generateBatchResourcesStatus(os);

		os.write("</body></html>".getBytes(ENCODING));
	}

	/**
	 * Serve icons
	 * 
	 * @param id
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	protected void doGetResource(String id, HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		String location = null;
		if (STATUS_OK.equals(id)) {
			location = "/org/freedesktop/tango/22x22/status/weather-clear.png";
		} else if (STATUS_WARN.equals(id)) {
			location = "/org/freedesktop/tango/22x22/status/weather-overcast.png";
		} else if (STATUS_ERROR.equals(id)) {
			location = "/org/freedesktop/tango/22x22/status/weather-severe-alert.png";
		} else if (STATUS_PROP.equals(id)) {
			location = "/org/freedesktop/tango/22x22/actions/format-justify-fill.png";
		}

		InputStream is = this.getClass().getResourceAsStream(location);
		OutputStream os = resp.getOutputStream();
		IOUtils.copy(is, os);
	}

	private void generateBatchResourcesStatus(ServletOutputStream os)
			throws IOException, UnsupportedEncodingException {
		os.write("<h2>Batch Status</h2>".getBytes(ENCODING));
		os.write("<table>".getBytes(ENCODING));
		os.write("<tr><th></th><th>Name</th><th>Description</th><th>Code</th><th>Resolution</th><th>Details</th></tr>"
				.getBytes(ENCODING));

		for (IStatusResource resource : status.getMonitoredResourcesStatus()) {
			if (resource.getType().equals(ResourceType.JOB.getLabel())) {
				IStatusResult r = resource.getStatus();
				generateRow(
						os,
						getStatus(r),
						resource.getName(),
						r.getDescription(),
						String.valueOf(r.getCode()),
						r.getResolutionSteps(),
						generateJobResourceDetails((IStatusJobResource) resource));
			}
		}
		os.write("</table>".getBytes(ENCODING));
	}

	private void generateConfiguration(ServletOutputStream os)
			throws IOException, UnsupportedEncodingException {
		os.write("<h2>Configuration</h2>".getBytes(ENCODING));
		Map<String, Map<String, String>> properties = status.getConfiguration();
		os.write("<table>".getBytes(ENCODING));
		os.write("<tr><th></th><th>Category</th><th>Name</th><th>Value</th></tr>"
				.getBytes(ENCODING));

		for (Entry<String, Map<String, String>> cat : properties.entrySet()) {
			String category = cat.getKey();

			for (Entry<String, String> r : cat.getValue().entrySet()) {
				generateRow(os, STATUS_PROP, category, r.getKey(), r.getValue());
			}

		}
		os.write("</table>".getBytes(ENCODING));
	}

	private String generateJobResourceDetails(IStatusJobResource resource) {
		StringBuilder details = new StringBuilder();
		details.append("<p><h3>Running jobs</h3>");
		details.append(generateRunningJobsDetails(resource
				.getCurrentJobExecutionsStatus()));
		details.append("</p>");
		details.append("<p><h3>Last executed jobs</h3>");
		details.append("Not yet implemented");
		details.append("</p>");
		details.append("<p><h3>Next firing jobs</h3>");
		details.append("Not yet implemented");
		details.append("</p>");
		return details.toString();
	}

	private StringBuilder generateRejectedItemList(List<String> rejectedItemsId) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String rejectedItemId : rejectedItemsId) {
			sb.append(rejectedItemId);
			i++;
			if (i < rejectedItemsId.size()) {
				sb.append(" , ");
			}
			if (i % 10 == 0) {
				sb.append("<br/>");
			}
		}
		return sb;
	}

	private void generateResourcesStatus(ServletOutputStream os)
			throws IOException, UnsupportedEncodingException {
		os.write("<h2>Resource Status</h2>".getBytes(ENCODING));
		os.write("<table>".getBytes(ENCODING));
		os.write("<tr><th></th><th>Name</th><th>Description</th><th>Code</th><th>Resolution</th></tr>"
				.getBytes(ENCODING));

		for (IStatusResource resource : status.getMonitoredResourcesStatus()) {
			if (resource.getType().equals(ResourceType.DEFAULT.getLabel())) {
				IStatusResult r = resource.getStatus();
				generateRow(os, getStatus(r), resource.getName(),
						r.getDescription(), String.valueOf(r.getCode()),
						r.getResolutionSteps());
			}
		}
		os.write("</table>".getBytes(ENCODING));
	}

	/**
	 * Outputs one table row
	 * 
	 * @param os
	 * @param status
	 * @param cols
	 * @throws IOException
	 */
	private void generateRow(ServletOutputStream os, String status,
			Object... cols) throws IOException {
		os.write("<tr>".getBytes());

		os.write(("<td><img src='?icon=" + status + "'></td>")
				.getBytes(ENCODING));

		for (Object obj : cols) {
			os.write("<td>".getBytes());
			if (obj != null) {
				os.write(obj.toString().getBytes(ENCODING));
			}
			os.write("</td>".getBytes());

		}
		os.write("</tr>".getBytes());
	}

	private Object generateRunningJobsDetails(
			List<IStatusJobExecutionResource> currentJobExecutionsStatus) {
		StringBuilder runningJobExecutionsDetails = new StringBuilder();
		runningJobExecutionsDetails.append("<table>");
		runningJobExecutionsDetails
				.append("<tr><th>Execution id</th><th>Start date</th><th>Job Status</th><th>Job progress</th><th>Rejected items</th></tr>");
		for (IStatusJobExecutionResource jobExecutionStatus : currentJobExecutionsStatus) {
			runningJobExecutionsDetails
					.append("<tr><td>")
					.append(jobExecutionStatus.getUid())
					.append("</td><td>")
					.append(DateFormat.getDateTimeInstance(DateFormat.SHORT,
							DateFormat.FULL).format(
							jobExecutionStatus.getStartDate()))
					.append("</td><td>")
					.append(jobExecutionStatus.getJobStatus())
					.append("</td><td>")
					.append(NumberFormat.getPercentInstance().format(
							jobExecutionStatus.getProgressStatus()))
					.append("</td><td>")
					.append(generateRejectedItemList(jobExecutionStatus
							.getRejectedItemsId())).append("</td></tr>");
		}
		runningJobExecutionsDetails.append("</table>");
		return runningJobExecutionsDetails.toString();
	}

	private void generateServiceResourcesStatus(ServletOutputStream os)
			throws IOException, UnsupportedEncodingException {
		os.write("<h2>Service Status</h2>".getBytes(ENCODING));
		os.write("<table>".getBytes(ENCODING));
		os.write("<tr><th></th><th>Name</th><th>Description</th><th>Code</th><th>Resolution</th><th>Statistics</th></tr>"
				.getBytes(ENCODING));

		for (IStatusResource resource : status.getMonitoredResourcesStatus()) {
			if (resource.getType().equals(ResourceType.SERVICE.getLabel())) {
				IStatusResult r = resource.getStatus();
				generateRow(
						os,
						getStatus(r),
						resource.getName(),
						r.getDescription(),
						String.valueOf(r.getCode()),
						r.getResolutionSteps(),
						generateServiceResourceStatistics((IStatusServiceResource) resource));
			}
		}
		os.write("</table>".getBytes(ENCODING));
	}

	private String generateServiceResourceStatistics(
			IStatusServiceResource resource) {
		StringBuilder statistics = new StringBuilder();
		statistics.append("<table>");
		statistics
				.append("<tr><th>Operation</th><th>Average Response Time (ms)</th><th>Average request flow (req/s)</th></tr>");
		List<String> operations = resource.getOperationNames();
		for (String operation : operations) {
			statistics
					.append("<tr><td>")
					.append(operation)
					.append("</td><td>")
					.append(NumberFormat.getNumberInstance().format(
							resource.getAverageResponseTime(operation)))
					.append("</td><td>")
					.append(NumberFormat.getNumberInstance().format(
							resource.getAverageFlow(operation)))
					.append("</td></tr>");
		}
		statistics.append("</table>");
		return statistics.toString();
	}

	/**
	 * Returns status icon id.
	 * 
	 * @param result
	 * @return
	 */
	private String getStatus(IStatusResult result) {

		if (result.isFatal()) {
			return STATUS_ERROR;
		}

		if (result.getCode() == IStatusResult.OK) {
			return STATUS_OK;
		}

		return STATUS_WARN;
	}

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			InputStream is = StatusServlet.class
					.getResourceAsStream("/status-web-conf.properties");

			if (is == null) {
				logger.warn("/status-web-conf.properties not found in classpath. Using default configuration");
			} else {
				Properties p = new Properties();
				p.load(is);
				is.close();
				allow = (String) p.get("ip.allow");
				useSpring = Boolean.parseBoolean((String) p.get("useSpring"));
			}
		} catch (Exception e) {
			logger.error(
					"Error loading configuration from /status-web-conf.properties.",
					e);
		}

		status = new StatusService();

		if (useSpring) {
			status.setObjectInstanciationListener(new SpringObjectInstantiationListener(
					this.getServletContext()));
		}

		status.setServletContextProvider(new IServletContextProvider() {
			public ServletContext getServletContext() {
				return StatusServlet.this.getServletContext();
			}
		});
		status.init();
	}
}
