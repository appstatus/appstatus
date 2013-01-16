package net.sf.appstatus.support.spring.cache;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * This is an adapter for Spring cache manager which wraps returned cache
 * instances.
 * 
 * <p>
 * When the cache returns a value and AppStatus is used with useThreadLocal and
 * a service call is active (IServiceMonitor#beginCall was called), the current
 * service call is flagged as "cacheHit".
 * 
 * <p>
 * Usage : simply add AppStatusCacheManager as your cacheManager and reference
 * your previous spring cache manager in your Spring config files.
 * <p>
 * With EhCacheManager :
 * 
 * <pre>
 * &lt;bean id="cacheManager" class="net.sf.appstatus.support.spring.cache.AppStatusCacheManager" p:cache-manager-ref="ehCacheCacheManager"/>
 * 
 * 
 * &lt;bean id="ehCacheCacheManager" class="org.springframework.cache.ehcache.EhCacheCacheManager" p:cache-manager-ref="ehcache"/>
 * 
 * &lt;!-- Ehcache library setup -->
 * &lt;bean id="ehcache" class="org.springframework.cache.ehcache.EhCacheManagerFactoryBean" p:config-location="ehcache.xml"/>
 * </pre>
 * 
 * 
 * @author Nicolas Richeton
 * 
 */
public class AppStatusCacheManager implements CacheManager {
	private CacheManager cacheManager;

	private final ConcurrentMap<String, AppStatusCacheAdapter> cacheMap = new ConcurrentHashMap<String, AppStatusCacheAdapter>(
			16);

	public AppStatusCacheManager() {
	}

	public Cache getCache(String name) {
		Cache cache = cacheManager.getCache(name);
		AppStatusCacheAdapter result = cacheMap.get(cache.getName());

		if (result == null) {
			result = new AppStatusCacheAdapter(cache);
			cacheMap.put(cache.getName(), result);
		}

		return result;
	}

	public Collection<String> getCacheNames() {
		return cacheManager.getCacheNames();
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
}
