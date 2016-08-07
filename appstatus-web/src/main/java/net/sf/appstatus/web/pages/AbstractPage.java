/*
 * Copyright 2010-2012 Capgemini
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.sf.appstatus.web.pages;

import static net.sf.appstatus.web.HtmlUtils.applyLayout;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;

import net.sf.appstatus.web.IPage;
import net.sf.appstatus.web.StatusWebHandler;

public abstract class AbstractPage implements IPage {
	private static final String ENCODING = "UTF-8";

	private static final String PAGELAYOUT = "pageLayout.html";

	private static final String URL = "http://appstatus.sourceforge.net/";

	public abstract void doGet(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException;

	public abstract void doPost(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp);

	public String getId() {
		return null;
	}

	public String getName() {
		return getId();
	}

	protected String getPage(final StatusWebHandler webHandler, final Map<String, String> valueMap)
			throws UnsupportedEncodingException, IOException {

		valueMap.put("css", "<link href=\"" + webHandler.getCssLocation() + "\" rel=\"stylesheet\">");

		valueMap.put("UrlAppStatus", URL);

		final StrBuilder menu = new StrBuilder();
		for (final String pageId : webHandler.getPages().keySet()) {
			final IPage page = webHandler.getPages().get(pageId);
			if (StringUtils.equals(pageId, getId())) {
				menu.append("<li class=active>");
			} else {
				menu.append("<li>");
			}
			menu.append("<a href=\"?p=" + page.getId() + "\">" + page.getName() + "</a></li>");
		}
		valueMap.put("menu", menu.toString());
		valueMap.put("applicationName", webHandler.getApplicationName());

		if (!valueMap.containsKey("js")) {
			valueMap.put("js", "");
		}

		return applyLayout(valueMap, PAGELAYOUT);

	}

	protected void setup(final HttpServletResponse resp, final String type) throws IOException {
		resp.setContentType(type);
		resp.setCharacterEncoding(ENCODING);
	}
}
