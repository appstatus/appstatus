package net.sf.appstatus.web.pages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.web.IPage;
import net.sf.appstatus.web.StatusWebHandler;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ServicesPageTest {

	/**
	 * https://sourceforge.net/apps/mantisbt/appstatus/view.php?id=71
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	@Test
	public void testMissingProperties() throws UnsupportedEncodingException, IOException {

		AppStatus appStatus = new AppStatus();

		StatusWebHandler statusWeb = new StatusWebHandler();
		statusWeb.setAppStatus(appStatus);
		statusWeb.setApplicationName("test");
		Map<String, IPage> pages = new HashMap<String, IPage>();
		StatusPage page = new StatusPage();
		pages.put(page.getId(), page);
		statusWeb.setPages(pages);
		statusWeb.init();

		HttpServletRequest servlet = Mockito.mock(HttpServletRequest.class);
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		final ByteArrayOutputStream writer = new ByteArrayOutputStream();

		Mockito.when(response.getOutputStream()).thenReturn(new ServletOutputStream() {

			@Override
			public void write(int b) throws IOException {
				writer.write(b);

			}
		});
		page.doGet(statusWeb, request, response);

		Assert.assertFalse(writer.toString("UTF-8").contains("${propertiesTable}"));
	}
}
