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
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.batch.IBatchManager;
import net.sf.appstatus.core.check.ICheckResult;
import net.sf.appstatus.web.IPage;
import net.sf.appstatus.web.StatusWebHandler;

/**
 * This is an Alpha radiator page. (Alpha version)
 * <p>
 * This page displays a quick overview of the application and reloads every
 * minute.
 * 
 * @author Nicolas Richeton
 * 
 */
public class RadiatorPage implements IPage {

	private static final int STATUS_ERROR = 2;
	private static final int STATUS_OK = 0;
	private static final int STATUS_WARN = 1;

	public void doGet(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException {

		// Setup response
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");

		// Get Health checks
		List<ICheckResult> results = webHandler.getAppStatus().checkAll();
		int status = STATUS_OK;
		for (ICheckResult r : results) {

			if (r.getCode() != ICheckResult.OK && !r.isFatal() && status == STATUS_OK) {
				status = STATUS_WARN;
			}

			if (r.getCode() != ICheckResult.OK && r.isFatal()) {
				status = STATUS_ERROR;
				break;
			}
		}

		String btnClass = "btn-success";
		if (status == STATUS_WARN) {
			btnClass = "btn-warning";
		}

		if (status == STATUS_ERROR) {
			btnClass = "btn-danger";
		}

		// Get batchs status.
		IBatchManager manager = webHandler.getAppStatus().getBatchManager();

		String bathStatus = manager.getErrorBatches().size() > 0 ? " progress-danger " : " progress-success ";
		String active = manager.getRunningBatches().size() > 0 ? " progress-striped active " : "";
		String width = manager.getRunningBatches().size() + manager.getFinishedBatches().size() > 0 ? "100%" : "0%";

		Writer w = resp.getWriter();
		w.append("<html>");
		w.append("<head>");
		w.append("<meta http-equiv=\"refresh\" content=\"60;\">");
		w.append("<link href=\"?resource=appstatus.css\" rel=\"stylesheet\">");
		w.append("</head>");
		w.append("<body style=\"background: #000; text-align: center; padding-top: 5%;\">");
		w.append("<p style=\"color: #fff; font-size: 200%;\" >" + webHandler.getApplicationName() + "</p>");
		w.append("<p style=\" padding-top: 10%;\"><a href=\"#\" class=\"btn btn-large " + btnClass
				+ "\" >Status</a></p>");
		w.append("<div class=\"progress "
				+ bathStatus
				+ active
				+ "\" style=\"margin-top: 5%; width: 90%; margin-left: 5%; margin-right: 5%;\">  <div class=\"bar\" style=\"width: "
				+ width + "%;\"></div></div>");
		w.append("</body></html>");
	}

	public void doPost(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp) {
		// Nothing to do
	}

	public String getId() {
		return "radiator";
	}

	public String getName() {
		return "Radiator";
	}

}
