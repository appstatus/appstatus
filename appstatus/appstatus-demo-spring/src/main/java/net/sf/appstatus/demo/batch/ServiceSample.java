package net.sf.appstatus.demo.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

/**
 * Sample service with caching using spring-cache.
 * 
 * @author Nicolas Richeton
 * 
 */
public class ServiceSample {
	private static Logger logger = LoggerFactory.getLogger(ServiceSample.class);

	@Cacheable("refCache")
	public String getRefs() {
		logger.info("getRefs");
		return "refList";
	}
}
