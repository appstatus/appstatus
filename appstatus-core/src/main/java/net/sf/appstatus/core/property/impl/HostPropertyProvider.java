package net.sf.appstatus.core.property.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import net.sf.appstatus.core.property.IPropertyProvider;

/**
 * Provides local host name and ip.
 * 
 * @author Nicolas Richeton
 */
public class HostPropertyProvider implements IPropertyProvider {

	public String getCategory() {
		return "Host";
	}

	public Map<String, String> getProperties() {
		String localhostIp;
		String localhostName;

		try {
			InetAddress addr = InetAddress.getLocalHost();
			localhostIp = addr.getHostAddress();
			localhostName = addr.getCanonicalHostName();
		} catch (UnknownHostException e) {
			localhostIp = "Not available";
			localhostName = "Not available";
		}

		Map<String, String> result = new HashMap<String, String>(3);
		result.put("name", localhostName);
		result.put("ip", localhostIp);

		return result;
	}

}
