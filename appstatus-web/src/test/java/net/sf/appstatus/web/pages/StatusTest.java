package net.sf.appstatus.web.pages;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.NullWriter;
import org.junit.Test;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.web.IPage;
import net.sf.appstatus.web.StatusWebHandler;

public class StatusTest {

	@Test
	public void testMaintenance() throws UnsupportedEncodingException, IOException {

		final AppStatus appStatus = new AppStatus();

		// Maintenance is on
		appStatus.setMaintenance(true);

		final StatusWebHandler statusWeb = new StatusWebHandler();
		statusWeb.setAppStatus(appStatus);
		statusWeb.setApplicationName("test");
		final Map<String, IPage> pages = new HashMap<String, IPage>();
		final StatusPage page = new StatusPage();
		pages.put(page.getId(), page);
		statusWeb.setPages(pages);
		statusWeb.init();

		final HttpServletRequest servlet = mock(HttpServletRequest.class);
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);

		final StubServletOutputStream sos = new StubServletOutputStream();

		when(response.getWriter()).thenReturn(new PrintWriter(new NullWriter()));
		when(response.getOutputStream()).thenReturn(sos);

		page.doGet(statusWeb, request, response);

		verify(response).setStatus(503);

	}

	@Test
	public void testSuccess() throws UnsupportedEncodingException, IOException {

		final AppStatus appStatus = new AppStatus();
		final StatusWebHandler statusWeb = new StatusWebHandler();
		statusWeb.setAppStatus(appStatus);
		statusWeb.setApplicationName("test");
		final Map<String, IPage> pages = new HashMap<String, IPage>();
		final StatusPage page = new StatusPage();
		pages.put(page.getId(), page);
		statusWeb.setPages(pages);
		statusWeb.init();

		final HttpServletRequest servlet = mock(HttpServletRequest.class);
		final HttpServletRequest request = mock(HttpServletRequest.class);
		final HttpServletResponse response = mock(HttpServletResponse.class);

		final StubServletOutputStream sos = new StubServletOutputStream();
		when(response.getWriter()).thenReturn(new PrintWriter(new NullWriter()));
		when(response.getOutputStream()).thenReturn(sos);

		page.doGet(statusWeb, request, response);

		verify(response).setStatus(200);

	}

}
