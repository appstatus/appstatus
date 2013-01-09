package net.sf.appstatus.support.spring.cache;

import net.sf.appstatus.core.services.IServiceMonitor;
import net.sf.appstatus.core.services.ServiceMonitorLocator;

import org.springframework.cache.Cache;

/**
 * A simple adapter for Spring Caches with flags the current service call as
 * "cacheHit".
 * 
 * @author Nicolas Richeton
 * 
 */
public class AppStatusCacheAdapter implements Cache {

	Cache cache;

	public AppStatusCacheAdapter(Cache ehcache) {
		this.cache = ehcache;
	}

	public void clear() {
		cache.clear();
	}

	public void evict(Object key) {
		cache.evict(key);
	}

	public ValueWrapper get(Object key) {
		ValueWrapper result = cache.get(key);

		if (result != null) {
			IServiceMonitor monitor = ServiceMonitorLocator.getCurrentServiceMonitor();
			if (monitor != null) {
				monitor.cacheHit();
			}
		}
		return result;
	}

	public String getName() {
		return cache.getName();
	}

	public Object getNativeCache() {
		return cache.getNativeCache();
	}

	public void put(Object key, Object value) {
		cache.put(key, value);
	}
}
