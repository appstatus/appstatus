package net.sf.appstatus.services;
import java.util.List;
import java.util.Properties;

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
		Assert.assertEquals(0d, myService.getAvgNestedCalls());

		ism = a.getServiceMonitor("service-name", "service-group");

		ism.beginCall();
		ism.executionTime(200);
		ism.endCall();

		Assert.assertEquals(150d, myService.getAvgResponseTime());
		Assert.assertEquals(200, myService.getMaxResponseTime().longValue());
		Assert.assertEquals(100, myService.getMinResponseTime().longValue());
		Assert.assertEquals(0d, myService.getAvgNestedCalls());

	}
	
	
	@Test
	public void testServiceMonitorWithDelay() {

		AppStatus a = new AppStatus();
		
		Properties p = new Properties();
		p.setProperty("services.minMaxDelay", "1");
		a.setConfiguration(p);
		
		a.init();

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
		Assert.assertNull(myService.getMaxResponseTime());
		Assert.assertNull( myService.getMinResponseTime());

		ism = a.getServiceMonitor("service-name", "service-group");

		ism.beginCall();
		ism.executionTime(200);
		ism.endCall();

		Assert.assertEquals(150d, myService.getAvgResponseTime());
		Assert.assertEquals(200, myService.getMaxResponseTime().longValue());
		Assert.assertEquals(200, myService.getMinResponseTime().longValue());

	}


	@Test
	public void testNestedServiceCall() {

		AppStatus a = new AppStatus();

		IServiceMonitor ism = a.getServiceMonitor("service-name", "service-group");

		ism.beginCall();
		ism.executionTime(100);
		ism.nestedCall();
		ism.nestedCall();
		ism.endCall();

		List<IService> services = a.getServices();

		IService myService = null;
		for (IService s : services) {
			if (s.getName().equals("service-name") && s.getGroup().equals("service-group"))
				myService = s;
		}
		Assert.assertNotNull(myService);
		Assert.assertEquals(2d, myService.getAvgNestedCalls());

		ism = a.getServiceMonitor("service-name", "service-group");

		ism.beginCall();
		ism.executionTime(200);
		ism.nestedCall();
		ism.endCall();

		Assert.assertEquals(1.5d, myService.getAvgNestedCalls());

	}
}
