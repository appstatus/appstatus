package net.sf.appstatus.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import net.sf.appstatus.core.services.IService;
import net.sf.appstatus.core.services.IServiceManager;
import net.sf.appstatus.core.services.IServiceMonitor;

public class InProcessServiceManager implements IServiceManager {

	Hashtable<String, IService> services = new Hashtable<String, IService>();

	public IServiceMonitor getMonitor(IService service) {
		return new ServiceCall( (Service)service);
	}

	public List<IService> getServices() {
		return new ArrayList<IService>(services.values());
	}

	public IService getService(String name, String group) {
		IService result = services.get(group + "/" + name);
		if (result == null) {
			synchronized (this) {
				result = services.get(group + "/" + name);
				if (result == null) {
					Service newService = new Service();
					newService.setName(name);
					newService.setGroup(group);
					services.put(group + "/" + name, newService);
					result = newService;
				}
			}
		}

		return result;
	}

	
}
