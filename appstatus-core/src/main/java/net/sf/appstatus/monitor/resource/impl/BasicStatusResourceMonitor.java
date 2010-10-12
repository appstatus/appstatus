package net.sf.appstatus.monitor.resource.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.appstatus.IPropertyProvider;
import net.sf.appstatus.IStatusChecker;
import net.sf.appstatus.IStatusResult;
import net.sf.appstatus.monitor.resource.IStatusResourceMonitor;

public class BasicStatusResourceMonitor implements IStatusResourceMonitor {

	private final String name;

	private final String type;

	private final String id;

	private final Set<IStatusChecker> statusCheckers;

	private final Set<IPropertyProvider> propertyProviders;

	public BasicStatusResourceMonitor(String id, String name, String type,
			Set<IStatusChecker> statusCheckers,
			Set<IPropertyProvider> propertyProviders) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.statusCheckers = statusCheckers;
		this.propertyProviders = propertyProviders;
	}

	public Map<String, Map<String, String>> getConfiguration() {
		Map<String, Map<String, String>> configuration = null;
		if (propertyProviders != null) {
			configuration = new HashMap<String, Map<String, String>>();
			for (IPropertyProvider propertyProvider : propertyProviders) {
				configuration.put(propertyProvider.getCategory(),
						propertyProvider.getProperties());
			}
		}
		return configuration;
	}

	public Map<String, Map<String, String>> getDetails() {
		return null;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<IStatusResult> getStatus() {
		List<IStatusResult> results = null;
		if (statusCheckers != null) {
			results = new ArrayList<IStatusResult>();
			for (IStatusChecker statusChecker : statusCheckers) {
				results.add(statusChecker.checkStatus());
			}
		}
		return results;
	}

	public String getType() {
		return type;
	}

}
