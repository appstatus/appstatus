package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.services.IService;
import net.sf.appstatus.web.HtmlUtils;
import net.sf.appstatus.web.StatusWebHandler;

import org.apache.commons.lang3.text.StrBuilder;

public class ServicesPage extends AbstractPage {
	private static final String ENCODING = "UTF-8";
	private static final String PAGECONTENTLAYOUT = "servicesContentLayout.html";

	@Override
	public void doGet(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException {

		setup(resp, "text/html");
		ServletOutputStream os = resp.getOutputStream();

		Map<String, String> valuesMap = new HashMap<String, String>();

		List<IService> services = webHandler.getAppStatus().getServices();
		Collections.sort(services);

		StrBuilder sbServicesTable = new StrBuilder();

		if (HtmlUtils.generateBeginTable(sbServicesTable, services.size())) {

			HtmlUtils.generateHeaders(sbServicesTable, "", "Group", "Name", "Hits", "Cache", "Running", "min", "max",
					"avg", "min (cached)", "max (cached)", "avg (cached)", "Errors", "Failures", "Hit rate");

			for (IService service : services) {
				HtmlUtils.generateRow(sbServicesTable, Resources.STATUS_JOB, service.getGroup(), service.getName(),
						service.getHits(),
						service.getCacheHits() + getPercent(service.getCacheHits(), service.getHits()),
						service.getRunning(), service.getMinResponseTime(), service.getMaxResponseTime(),
						Math.round(service.getAvgResponseTime()), service.getMinResponseTimeWithCache(),
						service.getMaxResponseTimeWithCache(), Math.round(service.getAvgResponseTimeWithCache()),
						service.getErrors() + getPercent(service.getErrors(), service.getHits()), service.getFailures()
								+ getPercent(service.getFailures(), service.getHits()),
						getRate(service.getCurrentRate()));
			}

			HtmlUtils.generateEndTable(sbServicesTable, services.size());
		}

		// generating content
		valuesMap.put("servicesTable", sbServicesTable.toString());
		String content = HtmlUtils.applyLayout(valuesMap, PAGECONTENTLAYOUT);

		valuesMap.clear();
		valuesMap.put("content", content);
		// generating page
		os.write(getPage(webHandler, valuesMap).getBytes(ENCODING));
	}

	@Override
	public void doPost(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp) {

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
