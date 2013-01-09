package net.sf.appstatus.services;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import net.sf.appstatus.core.services.IService;
import net.sf.appstatus.core.services.IServiceManager;
import net.sf.appstatus.core.services.IServiceMonitor;

/**
 * A service manager which stores service statistics in an ArrayList. There is
 * no history of calls so this implementation is safe event on production
 * systems (no memory increase over time).
 * <p>
 * Data are lost when restarting the application.
 * <p>
 * Reads the following configuration :
 * <ul>
 * <li>services.log.format</li>
 * <li>services.log</li>
 * </ul>
 * 
 * @author Nicolas Richeton
 * 
 */
public class InProcessServiceManager implements IServiceManager {

	Properties configuration = null;
	boolean log = true;
	String format = null;
	boolean useThreadLocal = false;

	Hashtable<String, IService> services = new Hashtable<String, IService>();

	/**
	 * {@inheritDoc}
	 */
	public IServiceMonitor getMonitor(IService service) {
		ServiceCall call = new ServiceCall((Service) service, log, useThreadLocal);

		if (format != null)
			call.setMessageFormat(format);
		return call;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IService> getServices() {
		return new ArrayList<IService>(services.values());
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 * 
	 * Init using "services.log.format", "services.log",
	 * "services.useThreadLocal" properties.
	 * 
	 */
	public void setConfiguration(Properties configuration) {
		this.configuration = configuration;

		if (configuration != null) {
			String confLogFormat = configuration.getProperty("services.log.format");

			if (confLogFormat != null)
				format = confLogFormat;

			String logEnabled = configuration.getProperty("services.log");
			if (logEnabled != null) {
				log = Boolean.valueOf(logEnabled);
			}

			useThreadLocal = Boolean.valueOf(configuration.getProperty("services.useThreadLocal"));
		}
	}

}
