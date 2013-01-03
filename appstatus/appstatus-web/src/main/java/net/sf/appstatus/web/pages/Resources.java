package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.web.StatusWebHandler;

import org.apache.commons.io.IOUtils;

public class Resources {

	public static final String LOGO = "logo";
	private static Map<String, String> resources = new HashMap<String, String>();
	public static final String STATUS_ERROR = "error";
	public static final String STATUS_JOB = "job";
	public static final String STATUS_JOB_ERROR = "job-error";
	public static final String STATUS_JOB_WARNING = "job-warning";
	public static final String STATUS_OK = "ok";
	public static final String STATUS_PROP = "prop";

	public static final String STATUS_WARN = "warn";
	static {
		resources.put(STATUS_OK,
				"/org/freedesktop/tango/22x22/status/weather-clear.png");
		resources.put(STATUS_WARN,
				"/org/freedesktop/tango/22x22/status/weather-overcast.png");
		resources.put(STATUS_ERROR,
				"/org/freedesktop/tango/22x22/status/weather-severe-alert.png");
		resources.put(STATUS_PROP,
				"/org/freedesktop/tango/22x22/actions/format-justify-fill.png");
		resources.put(STATUS_JOB,
				"/org/freedesktop/tango/22x22/emblems/emblem-system.png");
		resources.put(STATUS_JOB_ERROR,
				"/org/freedesktop/tango/22x22/status/dialog-error.png");
		resources.put(STATUS_JOB_WARNING,
				"/org/freedesktop/tango/22x22/status/dialog-warning.png");
		resources.put(LOGO, "/appstatus-logo.png");
	}

	public static void addResource(String id, String location) {
		resources.put(id, location);
	}

	public static void doGet(StatusWebHandler webHandler,
			HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String location = null;
		String id = req.getParameter("resource");
		if (id == null) {
			id = req.getParameter("icon");
		}

		if (resources.containsKey(id)) {
			location = resources.get(id);
			InputStream is = Resources.class.getResourceAsStream(location);
			IOUtils.copy(is, resp.getOutputStream());
		} else {
			resp.sendError(404);
		}

	}
}
