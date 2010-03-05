package net.sf.appstatus;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusService {
	private static Logger logger = LoggerFactory.getLogger(StatusService.class);
	private static final String CONFIG_LOCATION = "status-check.properties";

	static StatusService instance = new StatusService();
	static List<StatusChecker> probes;
	static List<PropertyProvider> propertyProviders;

	public static StatusService getInstance() {
		return instance;
	}

	public StatusService() {
		probes = new ArrayList<StatusChecker>();
		propertyProviders = new ArrayList<PropertyProvider>();

		try {
			// Load and init all probes
			Enumeration<URL> probesURLs;

			probesURLs = StatusService.class.getClassLoader().getResources(
					CONFIG_LOCATION);

			URL url = null;
			Properties p = null;
			InputStream is = null;
			while (probesURLs.hasMoreElements()) {
				url = probesURLs.nextElement();

				// Load plugin configuration
				p = new Properties();
				is = url.openStream();
				p.load(is);
				is.close();

				Set<String> keys = p.stringPropertyNames();
				for (String name : keys) {
					if (name.startsWith("check")) {
						String clazz = (String) p.get(name);
						StatusChecker check = (StatusChecker) Class.forName(
								clazz).newInstance();
						probes.add(check);
						logger.info("Registered status checker "+clazz);
					} else if (name.startsWith("property")) {
						String clazz = (String) p.get(name);
						PropertyProvider provider = (PropertyProvider) Class
								.forName(clazz).newInstance();
						propertyProviders.add(provider);
						logger.info("Registered property provider "+clazz);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Initialization error", e);
		}

	}

	public List<StatusResult> checkAll() {

		ArrayList<StatusResult> l = new ArrayList<StatusResult>();

		for (StatusChecker check : probes) {
			l.add(check.checkStatus());
		}
		return l;

	}

	public Map<String, String> getProperties() {

		TreeMap<String, String> l = new TreeMap<String, String>();

		for (PropertyProvider provider : propertyProviders) {
			l.putAll(provider.getProperties());
		}
		return l;
	}
}
