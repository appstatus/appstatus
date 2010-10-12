package net.sf.appstatus;

import static org.junit.Assert.fail;

import org.junit.Test;

public class StatusServiceTest {

	@Test
	public void testGetGlobalStatus() {
		try {
			StatusService statusService = new StatusService();
			statusService.init();
			statusService.getGlobalStatus();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetMonitoredResourcesStatus() {
		try {
			StatusService statusService = new StatusService();
			statusService.init();
			statusService.getMonitoredResourcesStatus();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testInit() {
		try {
			StatusService statusService = new StatusService();
			statusService.init();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
