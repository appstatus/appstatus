package net.sf.appstatus.core.loggers.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.appstatus.core.loggers.ILoggersManager;
import net.sf.appstatus.core.loggers.LoggerConfig;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/**
 * 
 * @author Romain Gonord
 * 
 */
public class LogbackLoggersManager implements ILoggersManager {
	public List<LoggerConfig> getLoggers() {
		List<LoggerConfig> loggers = new ArrayList<LoggerConfig>();
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		if (loggerFactory instanceof LoggerContext) {
			LoggerContext context = (LoggerContext) loggerFactory;
			for (ch.qos.logback.classic.Logger l : context.getLoggerList()) {
				loggers.add(new LoggerConfig(l.getName(), l.getEffectiveLevel().toString()));
			}
		}
		return loggers;
	}

	public void update(LoggerConfig logger2Change) {
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		if (loggerFactory instanceof LoggerContext) {
			LoggerContext context = (LoggerContext) loggerFactory;
			context.getLogger(logger2Change.getName()).setLevel(Level.valueOf(logger2Change.getLevel()));
		}
	}
}
