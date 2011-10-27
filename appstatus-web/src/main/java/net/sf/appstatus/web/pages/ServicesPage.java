package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.services.IService;
import net.sf.appstatus.web.HtmlUtils;

public class ServicesPage extends AbstractPage {
	private static final String ENCODING = "UTF-8";

	public static void render(AppStatus status, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException {

		setup(resp);
		ServletOutputStream os = resp.getOutputStream();
		begin(os);

		List<IService> services = status.getServices();

		os.write("<h1>Services</h1>".getBytes(ENCODING));

		if (HtmlUtils.generateBeginTable(os, services.size())) {

			HtmlUtils.generateHeaders(os, "", "Category", "Name", "Hits",
					"Cache", "Running", "min", "max", "avg", "min (cached)",
					"max (cached)", "avg (cached");

			for (IService service : services) {
				HtmlUtils.generateRow(os, Icons.STATUS_JOB, service.getGroup(),
						service.getName(), service.getHits(),
						service.getCacheHits(), service.getRunning(),
						service.getMinResponseTime(),
						service.getMaxResponseTime(),
						service.getAvgResponseTime(),
						service.getMinResponseTimeWithCache(),
						service.getMaxResponseTimeWithCache(),
						service.getAvgResponseTimeWithCache());
			}

			HtmlUtils.generateEndTable(os, services.size());
		}
		end(os);
	}
}
