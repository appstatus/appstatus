package net.sf.appstatus.support.aop;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.services.IServiceMonitor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

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
 * &lt;bean id="appStatusInterceptor" class="net.sf.appstatus.support.spring.AppStatusServiceInterceptor" scope="singleton">
 *         &lt;property name="appStatus" ref="appStatus" />
 * </bean>
 * 
 *  &lt;aop:config >
 *         &lt;aop:pointcut id="serviceCallPointcut" 
 *             expression="execution(public * your.package.ServiceClient*.*(..))" />
 *         &lt;aop:advisor id="serviceCallAdvisor" advice-ref="appStatusInterceptor" pointcut-ref="serviceCallPointcut" />
 *     &lt;/aop:config>
 * </pre>
 * 
 * @author Nicolas Richeton
 **/
public class AppStatusServiceInterceptor implements MethodInterceptor {

	private AppStatus appStatus;

	private IPostServiceCallback postServiceCallback;

	public Object invoke(MethodInvocation invocation) throws Throwable {
		IServiceMonitor m = appStatus.getServiceMonitor(invocation.getMethod().getName(), invocation.getThis()
				.getClass().getSimpleName());
		Object result;
		m.beginCall(invocation.getArguments());
		try {
			result = invocation.proceed();

			if (postServiceCallback != null) {
				postServiceCallback.handleResult(m, invocation);
			}

		} catch (Exception e) {
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

	public void setAppStatus(AppStatus appStatus) {
		this.appStatus = appStatus;
	}

}