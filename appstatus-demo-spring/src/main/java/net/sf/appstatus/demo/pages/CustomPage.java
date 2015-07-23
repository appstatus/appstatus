package net.sf.appstatus.demo.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.web.StatusWebHandler;
import net.sf.appstatus.web.pages.AbstractPage;

public class CustomPage extends AbstractPage {

	public void doGet(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException {
		setup(resp, "text/html");

		ServletOutputStream os = resp.getOutputStream();
		Map<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put("content", "Hello");
		os.write(getPage(webHandler, valuesMap).getBytes("UTF-8"));
		os.close();
	}

	public void doPost(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub

	}

	public String getId() {
		return "demo";
	}

	public String getName() {
		return "Demo";
	}

}
