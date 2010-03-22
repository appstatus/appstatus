package org.appstatus.jmx;

import java.util.List;
import java.util.Map;

import net.sf.appstatus.IStatusResult;
import net.sf.appstatus.StatusService;

public class AppStatusMBean {

	Map<String, Map<String, String>> getProperties() {
		return StatusService.getInstance().getProperties();
	}

	public List<IStatusResult> getStatus() {
		return StatusService.getInstance().checkAll();
	}
}
