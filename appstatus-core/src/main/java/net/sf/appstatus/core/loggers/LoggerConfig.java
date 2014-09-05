package net.sf.appstatus.core.loggers;

/**
 * 
 * @author Romain Gonord
 * 
 */
public class LoggerConfig implements Comparable<LoggerConfig> {
	private String level;
	private String name;

	public LoggerConfig() {
		// nothing to do
	}

	public LoggerConfig(String name, String level) {
		this.level = level;
		this.name = name;
	}

	public int compareTo(LoggerConfig logger) {
		return name.compareTo(logger.getName());
	}

	/* GETTER AND SETTER */
	public String getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public void setName(String name) {
		this.name = name;
	}
}
