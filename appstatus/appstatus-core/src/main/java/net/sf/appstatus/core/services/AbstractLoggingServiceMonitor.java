package net.sf.appstatus.core.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLoggingServiceMonitor implements IServiceMonitor {
	private static Logger stdLogger = LoggerFactory
			.getLogger(AbstractLoggingServiceMonitor.class);
	protected boolean cacheHit = false;
	protected Map<String, String> context = null;
	private final boolean enableLog;
	protected Long endTime = null;
	protected boolean error = false;
	protected String errorMessage = null;
	protected boolean failure = false;
	protected Exception failureException = null;
	protected String failureReason = null;
	private Logger logger = null;
	private String messageFormat = "${group}|${name}|${responseTime}|${cache}|${failure}|${failureReason}|${error}|${errorMessage}";
	protected Object[] parameters;
	private final IService service;
	protected long startTime;

	public AbstractLoggingServiceMonitor(IService service, boolean enableLog) {
		this.service = service;
		this.enableLog = enableLog;
	}

	public void beginCall(Object... parameters) {
		this.parameters = parameters;
	}

	public void cacheHit() {
		if (!this.cacheHit) {
			cacheHit = true;
		}
	}

	public void endCall() {
		if (endTime != null) {
			// endCall was called twice ! returning directly.
			return;
		}

		endTime = System.currentTimeMillis();

		if (enableLog) {
			Logger log = getLogger();
			if (log.isInfoEnabled()) {
				log.info(getLogMessage());

			}
		}
	}

	public void error(String message) {
		error = true;
		errorMessage = message;
	}

	public void failure(String reason) {
		failure(reason, null);
	}

	public void failure(String reason, Exception e) {
		this.failure = true;
		this.failureReason = reason;
		this.failureException = e;
	}

	protected Logger getLogger() {
		if (logger != null) {
			return logger;
		}

		return stdLogger;
	}

	/**
	 * Return the log message (at the end of the call).
	 * <p>
	 * This method can be overridden for very complex messages.
	 * 
	 * @return
	 */
	protected String getLogMessage() {
		long response = endTime - startTime;

		Map<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put("responseTime", String.valueOf(response));
		valuesMap.put("group", service.getGroup());
		valuesMap.put("name", service.getName());
		valuesMap.put("cache", cacheHit ? "HIT" : "MISS");
		valuesMap.put("failure", String.valueOf(failure));
		valuesMap.put("failureReason", failureReason != null ? failureReason
				: "");
		valuesMap.put(
				"failureException",
				failureException != null ? failureException
						.getLocalizedMessage() : "");
		valuesMap.put("error", String.valueOf(error));
		valuesMap.put("errorMessage", errorMessage != null ? errorMessage : "");

		StrSubstitutor sub = new StrSubstitutor(valuesMap);

		return sub.replace(messageFormat);
	}

	public void setLogger(Logger l) {
		logger = l;
	}

	/**
	 * http://commons.apache.org/lang/api-release/org/apache/commons/lang3/text/
	 * StrSubstitutor.html
	 * <ul>
	 * <li>group</li>
	 * <li>name</li>
	 * <li>responseTime</li>
	 * <li>cache</li>
	 * </ul>
	 * 
	 * @param messageFormat
	 */
	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}
}
