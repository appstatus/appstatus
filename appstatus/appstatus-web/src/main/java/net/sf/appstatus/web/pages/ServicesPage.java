package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.services.IService;
import net.sf.appstatus.web.HtmlUtils;
import net.sf.appstatus.web.StatusWebHandler;

public class ServicesPage extends AbstractPage {
	private static final String ENCODING = "UTF-8";

	@Override
	public void doGet(StatusWebHandler webHandler, HttpServletRequest req,
			HttpServletResponse resp) throws UnsupportedEncodingException,
			IOException {

		setup(resp, "text/html");
		ServletOutputStream os = resp.getOutputStream();
		begin(webHandler, os);

		List<IService> services = webHandler.getAppStatus().getServices();
		Collections.sort(services);

		os.write("<h1>Services</h1>".getBytes(ENCODING));

		if (HtmlUtils.generateBeginTable(os, services.size())) {

			HtmlUtils.generateHeaders(os, "", "Group", "Name", "Hits", "Cache",
					"Running", "min", "max", "avg", "min (cached)",
					"max (cached)", "avg (cached)", "Errors", "Failures");

			for (IService service : services) {
				HtmlUtils.generateRow(
						os,
						Resources.STATUS_JOB,
						service.getGroup(),
						service.getName(),
						service.getHits(),
						service.getCacheHits()
								+ getPercent(service.getCacheHits(),
										service.getHits()),
						service.getRunning(),
						service.getMinResponseTime(),
						service.getMaxResponseTime(),
						Math.round(service.getAvgResponseTime()),
						service.getMinResponseTimeWithCache(),
						service.getMaxResponseTimeWithCache(),
						Math.round(service.getAvgResponseTimeWithCache()),
						service.getErrors()
								+ getPercent(service.getErrors(),
										service.getHits()),
						service.getFailures()
								+ getPercent(service.getFailures(),
										service.getHits()));
			}

			HtmlUtils.generateEndTable(os, services.size());
		}
		end(os);
	}

	@Override
	public void doPost(StatusWebHandler webHandler, HttpServletRequest req,
			HttpServletResponse resp) {

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
		return " (" + ((100 * value1) / value2) + "%)";
	}
}
