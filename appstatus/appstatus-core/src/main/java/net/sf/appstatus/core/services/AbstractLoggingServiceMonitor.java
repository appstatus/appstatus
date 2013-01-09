package net.sf.appstatus.core.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default support implementation, with logging.
 * 
 * @author Nicolas Richeton
 * 
 */
public abstract class AbstractLoggingServiceMonitor implements IServiceMonitor {
	private static Logger stdLogger = LoggerFactory.getLogger(AbstractLoggingServiceMonitor.class);

	protected boolean cacheHit = false;
	protected Map<String, String> context = null;
	protected String correlationId = null;
	private final boolean enableLog;
	protected Long endTime = null;
	protected boolean error = false;
	protected String errorMessage = null;
	protected boolean failure = false;
	protected Exception failureException = null;
	protected String failureReason = null;
	private Logger logger = null;
	private String messageFormat = "${correlationId}|${group}|${name}|${responseTime}|${cache}|${status}|${statusMessage}";
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

	public void context(String name, String value) {
		if (context == null) {
			context = new HashMap<String, String>();
		}

		context.put(name, value);

	}

	public void correlationId(String correlationId) {
		this.correlationId = correlationId;
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
	 * Returns the log message (at the end of the call).
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
		valuesMap.put("failure", failure ? "FAILURE" : StringUtils.EMPTY);
		valuesMap.put("failureReason", StringUtils.defaultString(failureReason));
		valuesMap.put("failureException", failureException != null ? failureException.getLocalizedMessage() : "");
		valuesMap.put("error", error ? "ERROR" : StringUtils.EMPTY);
		valuesMap.put("errorMessage", StringUtils.defaultString(errorMessage));
		valuesMap.put("correlationId", StringUtils.defaultString(correlationId));

		// Populate context
		if (context != null) {
			Set<String> keys = context.keySet();
			for (String key : keys) {
				valuesMap.put(key, context.get(key));
			}
		}

		// Populate status
		String status = "SUCCESS";
		String statusMessage = StringUtils.EMPTY;
		if (error) {
			status = "ERROR";
			statusMessage = errorMessage;
		}
		if (failure) {
			status = "FAILURE";
			statusMessage = failureReason;
		}
		valuesMap.put("status", status);
		valuesMap.put("statusMessage", statusMessage);

		StrSubstitutor sub = new StrSubstitutor(valuesMap);

		return sub.replace(messageFormat);
	}

	public void setLogger(Logger l) {
		logger = l;
	}

	/**
	 * Set the log message format, based on :
	 * http://commons.apache.org/lang/api-release/org/apache/commons/lang3/text/
	 * StrSubstitutor.html
	 * 
	 * <ul>
	 * <li>group</li>
	 * <li>name</li>
	 * <li>responseTime</li>
	 * <li>cache</li>
	 * <li>failure</li>
	 * <li>failureReason</li>
	 * <li>failureException</li>
	 * <li>error</li>
	 * <li>errorMessage</li>
	 * <li>correlationId</li>
	 * <li>status : SUCCESS/FAILURE/ERROR</li>
	 * <li>statusMessage : failure or error message</li>
	 * <li>Any additional context values</li>
	 * </ul>
	 * 
	 * @param messageFormat
	 */
	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}
}
