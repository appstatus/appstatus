package net.sf.appstatus.services;
import java.util.List;

import junit.framework.Assert;
import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.services.IService;
import net.sf.appstatus.core.services.IServiceMonitor;

import org.junit.Test;

public class ServiceMonitorTest {

	@Test
	public void testServiceMonitor() {

		AppStatus a = new AppStatus();

		IServiceMonitor ism = a.getServiceMonitor("service-name", "service-group");

		ism.beginCall();
		ism.executionTime(100);
		ism.endCall();

		List<IService> services = a.getServices();

		IService myService = null;
		for (IService s : services) {
			if (s.getName().equals("service-name") && s.getGroup().equals("service-group"))
				myService = s;
		}
		Assert.assertNotNull(myService);
		Assert.assertEquals(100d, myService.getAvgResponseTime());
		Assert.assertEquals(100, myService.getMaxResponseTime().longValue());
		Assert.assertEquals(100, myService.getMinResponseTime().longValue());

		ism = a.getServiceMonitor("service-name", "service-group");

		ism.beginCall();
		ism.executionTime(200);
		ism.endCall();

		Assert.assertEquals(150d, myService.getAvgResponseTime());
		Assert.assertEquals(200, myService.getMaxResponseTime().longValue());
		Assert.assertEquals(100, myService.getMinResponseTime().longValue());

	}

}
