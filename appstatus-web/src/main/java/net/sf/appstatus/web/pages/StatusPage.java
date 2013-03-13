package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.check.ICheckResult;
import net.sf.appstatus.web.HtmlUtils;
import net.sf.appstatus.web.StatusWebHandler;

public class StatusPage extends AbstractPage {
	private static final String ENCODING = "UTF-8";

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
		begin(webHandler, os);

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

		os.write("<h2>Status Page</h2>".getBytes(ENCODING));
		os.write(("<p>Online:" + statusOk + "</p>").getBytes(ENCODING));
		os.write(("<p>Code:" + statusCode + "</p>").getBytes(ENCODING));

		os.write("<h3 class=\"status\">Status</h3>".getBytes(ENCODING));
		if (HtmlUtils.generateBeginTable(os, results.size())) {

			HtmlUtils.generateHeaders(os, "", "Group", "Name", "Description", "Code", "Resolution");

			for (ICheckResult r : results) {
				HtmlUtils.generateRow(os, getStatus(r), r.getGroup(), r.getProbeName(), r.getDescription(),
						String.valueOf(r.getCode()), r.getResolutionSteps());
			}
			HtmlUtils.generateEndTable(os, results.size());
		}

		os.write("<h3 class=\"properties\">Properties</h3>".getBytes(ENCODING));
		Map<String, Map<String, String>> properties = webHandler.getAppStatus().getProperties();
		if (HtmlUtils.generateBeginTable(os, properties.size())) {

			HtmlUtils.generateHeaders(os, "", "Group", "Name", "Value");

			for (Entry<String, Map<String, String>> cat : properties.entrySet()) {
				String category = cat.getKey();

				for (Entry<String, String> r : cat.getValue().entrySet()) {
					HtmlUtils.generateRow(os, Resources.STATUS_PROP, category, r.getKey(), r.getValue());
				}

			}
			HtmlUtils.generateEndTable(os, properties.size());
		}

		end(os);
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
