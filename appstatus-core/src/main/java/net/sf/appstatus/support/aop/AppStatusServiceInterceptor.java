package net.sf.appstatus.support.aop;

import net.sf.appstatus.core.services.IService;
import net.sf.appstatus.core.services.IServiceManager;
import net.sf.appstatus.core.services.IServiceMonitor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AOP AppStatus interceptor for services. Allows to remove references to
 * AppStatus in services calls (replaces
 * getMonitor/beginCall/cacheHit/failure/endCall).
 *
 * <p>
 * Supports failure and cacheHit (if useThreadLocal enabled).
 * <p>
 *
 * <pre>
 * &lt;bean id="appStatusInterceptor" class="net.sf.appstatus.support.aop.AppStatusServiceInterceptor" scope="singleton">
 *         &lt;property name="serviceManager" ref="serviceManager" />
 * 
 *         &lt;!-- Optional property for dynamic activation -->
 *         &lt;property name="activationCallback" ref="activationCallback" />
 * 
 *         &lt;!-- Optional property for logger selection -->
 *         &lt;property name="logger" value="&lt;logger-name>" />
 * 
 *         &lt;!-- Optional property for monitor setup -->
 *         &lt;property name="preServiceCallback" ref="preServiceCallback" />
 * 
 *         &lt;!-- Optional property for service result analysis -->
 *         &lt;property name="postServiceCallback" ref="postServiceCallback" />
 * 
 * &lt;/bean>
 * 
 *  &lt;aop:config >
 *         &lt;aop:advisor id="serviceCallAdvisor" advice-ref="appStatusInterceptor" pointcut="execution(public * your.package.ServiceClient*.*(..))" />
 *     &lt;/aop:config>
 * </pre>
 *
 * @author Nicolas Richeton
 **/
public class AppStatusServiceInterceptor implements MethodInterceptor {

	private IAppStatusActivationCallback activationCallback;
	private Logger logger = null;

	private IPostServiceCallback postServiceCallback;
	private IPreServiceCallback preServiceCallback;
	private IServiceManager serviceManager;

	public Object invoke(MethodInvocation invocation) throws Throwable {

		// Ensure AppStatus is activated
		if (activationCallback != null && !activationCallback.isActive(invocation)) {
			return invocation.proceed();
		}

		IServiceMonitor m = null;

		// Get custom monitor
		if (preServiceCallback != null) {
			m = preServiceCallback.getMonitor(serviceManager, invocation);
		}

		// Create default service monitor if necessary.
		if (m == null) {
			IService service = serviceManager.getService(invocation.getMethod().getName(), invocation.getThis()
					.getClass().getSimpleName());
			m = serviceManager.getMonitor(service);
		}

		// Change default logger
		if (logger != null) {
			m.setLogger(logger);
		}

		// Allow custom setup
		if (preServiceCallback != null) {
			preServiceCallback.setup(m, invocation);
		}

		Object result = null;
		m.beginCall(invocation.getArguments());
		try {
			result = invocation.proceed();

			// Allow custom result handling
			if (postServiceCallback != null) {
				postServiceCallback.handleResult(m, invocation, result);
			}

		} catch (Exception e) {
			// Allow custom exception handling
			if (postServiceCallback != null) {
				postServiceCallback.handleException(m, invocation, e);
			} else {
				m.failure(e.getLocalizedMessage(), e);
			}
			throw e;
		} finally {
			m.endCall();
		}
		return result;

	}

	/**
	 * @param activationCallback
	 */
	public void setActivationCallback(IAppStatusActivationCallback activationCallback) {
		this.activationCallback = activationCallback;
	}

	/**
	 * Set the logger to use with this interceptor.
	 *
	 * @param name
	 *            logger name
	 */
	public void setLogger(String name) {
		logger = LoggerFactory.getLogger(name);
	}

	/**
	 * Adding a callback disables automatic failure management. It's up to the
	 * callback to set failure flag.
	 *
	 * @param postServiceCallback
	 */
	public void setPostServiceCallback(IPostServiceCallback postServiceCallback) {
		this.postServiceCallback = postServiceCallback;
	}

	/**
	 * @param preServiceCallback
	 */
	public void setPreServiceCallback(IPreServiceCallback preServiceCallback) {
		this.preServiceCallback = preServiceCallback;
	}

	/**
	 * Set the AppStatus service manager to use for this interceptor.
	 *
	 * @param serviceManager
	 */
	public void setServiceManager(IServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

}