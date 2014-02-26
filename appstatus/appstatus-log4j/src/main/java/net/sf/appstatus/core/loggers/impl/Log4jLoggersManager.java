package net.sf.appstatus.core.loggers.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import net.sf.appstatus.core.loggers.ILoggersManager;
import net.sf.appstatus.core.loggers.LoggerConfig;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * 
 * @author Romain Gonord
 * 
 */
public class Log4jLoggersManager implements ILoggersManager {
	@SuppressWarnings("unchecked")
	public List<LoggerConfig> getLoggers() {
		List<LoggerConfig> loggers = new ArrayList<LoggerConfig>();
		loggers.add(new LoggerConfig("ROOT", LogManager.getRootLogger().getEffectiveLevel().toString()));
		Enumeration<Logger> currentLoggers = LogManager.getCurrentLoggers();
		while (currentLoggers.hasMoreElements()) {
			Logger logger = currentLoggers.nextElement();
			loggers.add(new LoggerConfig(logger.getName(), logger.getEffectiveLevel().toString()));
		}
		Collections.sort(loggers);
		return loggers;
	}

	public void update(LoggerConfig logger2Change) {

		if ("ROOT".equals(logger2Change.getName())) {
			LogManager.getRootLogger().setLevel(Level.toLevel(logger2Change.getLevel()));
		} else {
			LogManager.getLogger(logger2Change.getName()).setLevel(Level.toLevel(logger2Change.getLevel()));
		}
	}
}
