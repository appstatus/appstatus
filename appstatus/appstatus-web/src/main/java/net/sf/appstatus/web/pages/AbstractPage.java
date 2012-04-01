package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.AppStatus;

public abstract class AbstractPage {
	private static final String ENCODING = "UTF-8";

	private static final String styleSheet = "<style type=\"text/css\" media=\"screen\">"
			+ "h1 { font-size: 120%; }"
			+ "h2 { font-size: 110%; }"
			+ "p { font-size: 90%; }"
			+ "table { font-size: 80%; }"
			+ "table ,th, td {  border: 1px solid black; border-collapse:collapse; padding: 2px; }"
			+ "th { background-color: #DDDDDD; }"
			+ "td form { margin: 0; }"
			+ ".logo { float: right; font-size: 120%; }"
			+ ".menu {  padding: 5px; width: 15em; text-align: center;  border: 1px dashed black;  font-size: 90%; }"
			+ "</style>";
	private static final String URL = "http://appstatus.sourceforge.net/";

	protected void begin(ServletOutputStream os)
			throws UnsupportedEncodingException, IOException {
		os.write("<html><head>".getBytes(ENCODING));
		os.write(styleSheet.getBytes(ENCODING));
		os.write("</head>".getBytes(ENCODING));
		os.write("<body>".getBytes(ENCODING));

		os.write(("<div class=\"logo\"><a href=\"" + URL
				+ "\"><img src='?icon=" + Icons.LOGO + "'/></a></div>")
				.getBytes(ENCODING));
		os.write("<div class=\"menu\"><a href=\"?\">Status</a> | <a href=\"?p=services\">Services</a> | <a href=\"?p=batch\">Batches</a></div>"
				.getBytes(ENCODING));
	}

	public abstract void doGet(AppStatus status, HttpServletRequest req,
			HttpServletResponse resp) throws UnsupportedEncodingException,
			IOException;

	public abstract void doPost(AppStatus status, HttpServletRequest req,
			HttpServletResponse resp);

	protected void end(ServletOutputStream os)
			throws UnsupportedEncodingException, IOException {
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

	protected void setup(HttpServletResponse resp, String type)
			throws IOException {
		resp.setContentType(type);
		resp.setCharacterEncoding(ENCODING);
	}
}
