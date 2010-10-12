package net.sf.appstatus;

import org.junit.Test;

public class StatusServiceTest {

	@Test
	public void testGetGlobalStatus() {
		StatusService statusService = new StatusService();
		statusService.init();
		statusService.getGlobalStatus();
	}

	@Test
	public void testGetMonitoredResourcesStatus() {
		StatusService statusService = new StatusService();
		statusService.init();
		statusService.getMonitoredResourcesStatus();
	}

	@Test
	public void testInit() {
		StatusService statusService = new StatusService();
		statusService.init();
	}

}
