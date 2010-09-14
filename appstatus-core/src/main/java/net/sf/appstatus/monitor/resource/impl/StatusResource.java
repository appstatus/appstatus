package net.sf.appstatus.monitor.resource.impl;

import net.sf.appstatus.IStatusChecker;
import net.sf.appstatus.IStatusResult;
import net.sf.appstatus.monitor.resource.IStatusResource;

public class StatusResource implements IStatusResource {

	private final String name;

	private final String type;

	private final String uid;

	private final IStatusChecker statusChecker;

	public StatusResource(String name, String type, IStatusChecker statusChecker) {
		this.uid = name;
		this.name = name;
		this.type = type;
		this.statusChecker = statusChecker;
	}

	public String getName() {
		return name;
	}

	public IStatusResult getStatus() {
		return statusChecker.checkStatus();
	}

	public String getType() {
		return type;
	}

	public String getUid() {
		return uid;
	}

}
