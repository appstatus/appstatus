package net.sf.appstatus.core.loggers;

import java.util.List;

/**
 * 
 * @author Romain Gonord
 * 
 */
public interface ILoggersManager {
	public static final String LEVEL_DEBUG = "DEBUG";

	void update(LoggerConfig logger2Change);

	List<LoggerConfig> getLoggers();
}
