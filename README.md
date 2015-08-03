AppStatus
=========
## Intro 

AppStatus brings the information you need on developement and production to ensure everything is running and gives pointers when issue happens. 

This means: 
* Self tests  (compatible with nagios and other monitoring applications)
* Configuration information
* Controler and Service statistics, including hits, average response time, cache usage, cache efficiency, functional and error ratio, hit rate. 
* Batch progress and history including failures and invalid objects. 
* Radiator-compatible screen for monitoring


## Installation 

With Maven and Spring 

pom.xml
```xml
  <dependency>
    	<groupId>net.sf.appstatus </groupId>
    	<artifactId>appstatus-web</artifactId>
    	<version>0.7.0-SNAPSHOT</version>
    	<scope>compile</scope>
    </dependency>
``` 


web.xml
```xml
	<servlet>
		<servlet-name>status</servlet-name>
		<servlet-class>net.sf.appstatus.web.StatusServlet</servlet-class>
		<init-param>
			<param-name>bean</param-name>
			<param-value>appstatus</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>

	<servlet-mapping>
		<servlet-name>status</servlet-name>
		<url-pattern>/status</url-pattern>
	</servlet-mapping>
```

Spring context.xml

```xml
<bean id="appstatus" class="net.sf.appstatus.core.AppStatus"
		init-method="init" >
		<property name="objectInstanciationListener">
			<bean class="net.sf.appstatus.support.spring.SpringObjectInstantiationListener" />
		</property>
		<property name="batchManager">
			<bean class="net.sf.appstatus.batch.InProcessBatchManager"/>
		</property>
		<property name="serviceManager" ref="serviceManager" />

		<!-- Status Checkers -->
		<property name="checkers">
			<list>
				<bean class="net.sf.appstatus.core.check.impl.JvmCheck" />
				<bean class="net.sf.appstatus.core.check.impl.ServicesPerformanceCheck" />
				<bean class="net.sf.appstatus.core.check.impl.ServicesFailureCheck" />
				<!-- Insert your own here -->
			</list>
		</property>

		<!-- Property providers -->
		<property name="propertyProviders">
			<list>
				<bean class="net.sf.appstatus.core.property.impl.JvmPropertyProvider" />
				<bean class="net.sf.appstatus.core.property.impl.WarMavenVersionProvider" />
				<!-- Insert your own here -->
			</list>
		</property>
	</bean>

	<bean id="serviceManager" class="net.sf.appstatus.services.InProcessServiceManager">
		<!-- Configuration -->
		<property name="configuration">
			<props>
				<prop key="services.useThreadLocal">true</prop>
				<prop key="services.minMaxDelay">10</prop>
			</props>
		</property>
	</bean>

	<!-- Monitor your code with AOP -->
	<bean id="appStatusInterceptor"
		class="net.sf.appstatus.support.aop.AppStatusServiceInterceptor">
		<property name="serviceManager" ref="serviceManager" />
	</bean>

	<aop:config>
	<!-- Configure advisor to match all interesting code (usually Controlers, Services)-->
		<aop:advisor id="serviceCallAdvisor" advice-ref="appStatusInterceptor"
			pointcut="execution(public * package.Class.method())" />
	</aop:config>
```

## Website 

See http://appstatus.sourceforge.netfor additional details. 
