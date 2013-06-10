package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.web.HtmlUtils;
import net.sf.appstatus.web.StatusWebHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;

public abstract class AbstractPage {
	private static final String ENCODING = "UTF-8";

	private static final String PAGELAYOUT = "pageLayout.html";

	private static final String URL = "http://appstatus.sourceforge.net/";

	public abstract void doGet(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException;

	public abstract void doPost(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp);

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

	protected String getPage(StatusWebHandler webHandler, Map<String, String> valueMap)
			throws UnsupportedEncodingException, IOException {

		valueMap.put("css", "<link href=\"" + webHandler.getCssLocation() + "\" rel=\"stylesheet\">");

		valueMap.put("UrlAppStatus", URL);

		StrBuilder menu = new StrBuilder();
		for (String pageId : webHandler.getPages().keySet()) {
			AbstractPage page = webHandler.getPages().get(pageId);
			if (StringUtils.equals(pageId, getId())) {
				menu.append("<li class=active>");
			} else {
				menu.append("<li>");
			}
			menu.append("<a href=\"?p=" + page.getId() + "\">" + page.getName() + "</a></li>");
		}
		valueMap.put("menu", menu.toString());
		valueMap.put("applicationName", webHandler.getApplicationName());

		return HtmlUtils.applyLayout(valueMap, PAGELAYOUT);

	}

	protected void setup(HttpServletResponse resp, String type) throws IOException {
		resp.setContentType(type);
		resp.setCharacterEncoding(ENCODING);
	}
}
