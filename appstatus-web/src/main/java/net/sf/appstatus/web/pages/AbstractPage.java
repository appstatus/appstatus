package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractPage {
	private static final String ENCODING = "UTF-8";
	private static final String styleSheet = "<style type=\"text/css\" media=\"screen\">"
			+ "h1 { font-size: 120%; }"
			+ "h2 { font-size: 110%; }"
			+ "p { font-size: 90%; }"
			+ "table { font-size: 80%; }"
			+ "table ,th, td {  border: 1px solid black; border-collapse:collapse; padding: 2px; }"
			+ "th { background-color: #DDDDDD; }"
			+ ".logo { float: right; font-size: 120%; }"
			+ ".menu {  padding: 5px; width: 15em; text-align: center;  border: 1px dashed black;  font-size: 90%; }"
			+ "</style>";

	private static final String URL = "http://appstatus.sourceforge.net/";

	protected static void begin(ServletOutputStream os)
			throws UnsupportedEncodingException, IOException {
		os.write("<html><head>".getBytes(ENCODING));
		os.write(styleSheet.getBytes(ENCODING));
		os.write("</head>".getBytes(ENCODING));
		os.write("<body>".getBytes(ENCODING));

		os.write(("<div class=\"logo\"><a href=\"" + URL + "\">AppStatus</a></div>")
				.getBytes(ENCODING));
		os.write("<div class=\"menu\"><a href=\"?\">Status</a> | <a href=\"?services\">Services</a> | <a href=\"?batch\">Batches</a></div>"
				.getBytes(ENCODING));
	}

	protected static void end(ServletOutputStream os)
			throws UnsupportedEncodingException, IOException {
		os.write("</body></html>".getBytes(ENCODING));

	}

	protected static void setup(HttpServletResponse resp) throws IOException {

		resp.setContentType("text/html");
		resp.setCharacterEncoding(ENCODING);

	}
}
