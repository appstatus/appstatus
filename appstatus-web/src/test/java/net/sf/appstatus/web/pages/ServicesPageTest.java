package net.sf.appstatus.web.pages;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.web.IPage;
import net.sf.appstatus.web.StatusWebHandler;

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

		HttpServletRequest servlet = mock(HttpServletRequest.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		final ByteArrayOutputStream writer = new ByteArrayOutputStream();

		when(response.getOutputStream()).thenReturn(new ServletOutputStream() {

			@Override
			public void write(int b) throws IOException {
				writer.write(b);

			}
		});
		page.doGet(statusWeb, request, response);

		assertFalse(writer.toString("UTF-8").contains("${propertiesTable}"));
	}
}
