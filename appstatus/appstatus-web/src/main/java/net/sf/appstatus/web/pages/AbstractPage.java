package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.web.StatusWebHandler;

public abstract class AbstractPage {
	private static final String ENCODING = "UTF-8";

	private static final String URL = "http://appstatus.sourceforge.net/";

	protected void begin(StatusWebHandler webHandler, ServletOutputStream os) throws UnsupportedEncodingException,
			IOException {
		os.write("<html><head>".getBytes(ENCODING));
		os.write(("<link href=\"" + webHandler.getCssLocation() + "\" rel=\"stylesheet\">").getBytes(ENCODING));
		os.write("</head>".getBytes(ENCODING));
		os.write("<body>".getBytes(ENCODING));

		// Add Logo
		os.write(("<div class=\"logo\"><a href=\"" + URL + "\"><img src='?icon=" + Resources.LOGO + "'/></a></div>")
				.getBytes(ENCODING));

		// Add pages quick links

		os.write("<div class='menu'><ul class=\"nav nav-tabs\">".getBytes(ENCODING));
		for (String pageId : webHandler.getPages().keySet()) {
			AbstractPage page = webHandler.getPages().get(pageId);

			// <li class="active">
			os.write(("<li class=active><a href=\"?p=" + page.getId() + "\">" + page.getName() + "</a></li>")
					.getBytes(ENCODING));

		}

		os.write("</ul></div>".getBytes(ENCODING));

		os.write("<h1 class='brand'>".getBytes(ENCODING));

		os.write(webHandler.getApplicationName().getBytes(ENCODING));
		os.write("</h1>".getBytes(ENCODING));

	}

	public abstract void doGet(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException;

	public abstract void doPost(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp);

	protected void end(ServletOutputStream os) throws UnsupportedEncodingException, IOException {
		os.write("</body></html>".getBytes(ENCODING));
	}

	/**
	 * Returns page name, used in url to trigger page rendering.
	 * 
	 * @return
	 */
	public String getId() {
		return null;
	}

	public String getName() {
		return getId();
	}

	protected void setup(HttpServletResponse resp, String type) throws IOException {
		resp.setContentType(type);
		resp.setCharacterEncoding(ENCODING);
	}
}
