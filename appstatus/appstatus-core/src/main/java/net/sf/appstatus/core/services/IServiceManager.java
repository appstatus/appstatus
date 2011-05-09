package net.sf.appstatus.core.services;

import java.util.List;

public interface IServiceManager {

	IServiceMonitor getMonitor(IService batch);

	IService getService(String name, String group);

	List<IService> getServices();

}
