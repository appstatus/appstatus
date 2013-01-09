package net.sf.appstatus.support.cache.ehcache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.sf.appstatus.support.cache.AppStatusCacheAdapter;

import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;

/**
 * This is an adapter for Spring ehcache manager which wraps returned cache
 * instances.
 * 
 * <p>
 * When the cache returns a value and AppStatus is used with useThreadLocal and
 * a service call is active (IServiceMonitor#beginCall was called), the current
 * service call is flagged as "cacheHit".
 * 
 * <p>
 * Usage : simply replace EhCacheCacheManager by AppStatusEhCacheCacheManager in
 * your Spring config files.
 * 
 * @author Nicolas Richeton
 * 
 */
public class AppStatusEhCacheCacheManager extends EhCacheCacheManager {
	private final ConcurrentMap<String, AppStatusCacheAdapter> cacheMap = new ConcurrentHashMap<String, AppStatusCacheAdapter>(
			16);

	public AppStatusEhCacheCacheManager() {
	}

	public AppStatusEhCacheCacheManager(net.sf.ehcache.CacheManager cacheManager) {
		super(cacheManager);
	}

	@Override
	public Cache getCache(String name) {
		EhCacheCache ehCache = (EhCacheCache) super.getCache(name);
		AppStatusCacheAdapter result = cacheMap.get(ehCache.getName());

		if (result == null) {
			result = new AppStatusCacheAdapter(ehCache);
			cacheMap.put(ehCache.getName(), result);
		}

		return result;
	}

}
