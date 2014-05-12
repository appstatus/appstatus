package net.sf.appstatus.core.loggers;

import java.util.List;
import java.util.Properties;

/**
 * 
 * @author Romain Gonord
 * 
 */
public interface ILoggersManager {
	public static final String LEVEL_DEBUG = "DEBUG";

	/**
	 * Get current configuration.
	 * 
	 * @return
	 */
	Properties getConfiguration();

	List<LoggerConfig> getLoggers();

	void setConfiguration(Properties configuration);

	void update(LoggerConfig logger2Change);
}
