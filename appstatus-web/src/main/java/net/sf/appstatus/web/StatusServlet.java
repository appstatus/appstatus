package net.sf.appstatus.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.StatusResult;
import net.sf.appstatus.StatusService;

public class StatusServlet extends HttpServlet {

	String allow = null;
	private static final long serialVersionUID = 3912325072098291029L;

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			InputStream is = StatusServlet.class
					.getResourceAsStream("/status-web-conf.properties");

			Properties p = new Properties();
			p.load(is);

			is.close();
			allow = (String) p.get("ip.allow");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		if (allow != null) {
			if (!req.getRemoteAddr().equals(allow)) {
				resp.sendError(401, "IP not authorized");
				return;
			}
		}
		List<StatusResult> results = StatusService.getInstance().checkAll();
		boolean statusOk = true;
		int statusCode = 200;
		for (StatusResult r : results) {
			if (r.isFatal()) {
				resp.setStatus(500);
				statusCode = 500;
				statusOk = false;
				break;
			}
		}

		ServletOutputStream os = resp.getOutputStream();
		os.write("<h1>Status Page</h1>".getBytes());
		os.write(("<p>Online:" + statusOk + "</p>").getBytes());
		os.write(("<p>Code:" + statusCode + "</p>").getBytes());
		
		os.write( "<h2>Status</h2>".getBytes());
		os.write("<table border='1'>".getBytes());
		os
				.write("<tr><td>Name</td><td>Description</td><td>Code</td><td>Resolution</td></tr>"
						.getBytes());

		for (StatusResult r : results) {
			generateRow(os, r.getProbeName(), r.getDescription(), String
					.valueOf(r.getCode()), r.getResolutionSteps());
		}
		os.write("</table>".getBytes());

		os.write( "<h2>Properties</h2>".getBytes());
		Map<String, String> properties = StatusService.getInstance()
				.getProperties();
		os.write("<table border='1'>".getBytes());
		os.write("<tr><td>Name</td><td>Value</td></tr>".getBytes());

		for (Entry<String, String> r : properties.entrySet()) {
			generateRow(os, r.getKey(), r.getValue());
		}
		os.write("</table>".getBytes());
	}

	private void generateRow(ServletOutputStream os, Object... cols)
			throws IOException {
		os.write("<tr>".getBytes());

		for (Object obj : cols) {
			os.write("<td>".getBytes());
			if (obj != null) {
				os.write(obj.toString().getBytes());
			}
			os.write("</td>".getBytes());

		}
		os.write("</tr>".getBytes());
	}

}
