package net.sf.appstatus.web.pages;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.web.IPage;
import net.sf.appstatus.web.StatusWebHandler;

import org.apache.commons.io.output.NullWriter;
import org.junit.Test;
import org.mockito.Mockito;

public class RadiatorTest {

	/**
	 * https://sourceforge.net/apps/mantisbt/appstatus/view.php?id=70
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	@Test
	public void testNoBatchs() throws UnsupportedEncodingException, IOException {

		AppStatus appStatus = new AppStatus();

		StatusWebHandler statusWeb = new StatusWebHandler();
		statusWeb.setAppStatus(appStatus);
		statusWeb.setApplicationName("test");
		Map<String, IPage> pages = new HashMap<String, IPage>();
		RadiatorPage page = new RadiatorPage();
		pages.put(page.getId(), page);
		statusWeb.setPages(pages);
		statusWeb.init();

		HttpServletRequest servlet = Mockito.mock(HttpServletRequest.class);
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		Mockito.when(response.getWriter()).thenReturn(new PrintWriter(new NullWriter()));
		page.doGet(statusWeb, request, response);

	}
}
