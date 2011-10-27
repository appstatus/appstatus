package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

public class Icons {

	public static final String STATUS_ERROR = "error";
	public static final String STATUS_JOB = "job";
	public static final String STATUS_OK = "ok";
	public static final String STATUS_PROP = "prop";
	public static final String STATUS_WARN = "warn";

	public static void render(OutputStream os, String id) throws IOException {
		String location = null;
		if (STATUS_OK.equals(id)) {
			location = "/org/freedesktop/tango/22x22/status/weather-clear.png";
		} else if (STATUS_WARN.equals(id)) {
			location = "/org/freedesktop/tango/22x22/status/weather-overcast.png";
		} else if (STATUS_ERROR.equals(id)) {
			location = "/org/freedesktop/tango/22x22/status/weather-severe-alert.png";
		} else if (STATUS_PROP.equals(id)) {
			location = "/org/freedesktop/tango/22x22/actions/format-justify-fill.png";
		} else if (STATUS_JOB.equals(id)) {
			location = "/org/freedesktop/tango/22x22/emblems/emblem-system.png";
		}

		InputStream is = Icons.class.getResourceAsStream(location);
		IOUtils.copy(is, os);

	}
}
