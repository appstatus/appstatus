package net.sf.appstatus.core.services;

import java.util.List;

/**
 * Service manager. Entry point for using services.
 * 
 * @author Nicolas Richeton
 * 
 */
public interface IServiceManager {

	IServiceMonitor getMonitor(IService batch);

	IService getService(String name, String group);

	List<IService> getServices();

}
