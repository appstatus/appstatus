package org.appstatus.jmx;

import java.util.List;
import java.util.Map;

import net.sf.appstatus.IStatusResult;
import net.sf.appstatus.StatusService;

public class AppStatusMBean {
	StatusService service = null;
	
	public AppStatusMBean() {
		service = new StatusService();
	}
	

	public Map<String, Map<String, String>> getProperties() {
		return service.getProperties();
	}

	public List<IStatusResult> getStatus() {
		return service.checkAll();
	}
}
