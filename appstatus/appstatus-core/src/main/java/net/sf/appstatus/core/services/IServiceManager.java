package net.sf.appstatus.core.services;

import java.util.List;

/**
 * Service manager. Entry point for using services.
 * 
 * @author Nicolas Richeton
 * 
 */
public interface IServiceManager {

	/**
	 * Returns a monitor for a new service call.
	 * 
	 * @param service
	 * @return
	 */
	IServiceMonitor getMonitor(IService service);

	/**
	 * Create or get the service definition identified by group and name.
	 * 
	 * @param name
	 *            name of the service.
	 * @param group
	 *            group of the service.
	 * @return
	 */
	IService getService(String name, String group);

	/**
	 * List all current services.
	 * 
	 * <p>
	 * Services are defined using #getService(String, String)
	 * 
	 * @return
	 */
	List<IService> getServices();

}
