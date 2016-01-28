AppStatus
=========
## Intro 

AppStatus brings the information you need on developement and production to ensure everything is running and gives pointers when issue happens. 

This means: 
* Self tests  (compatible with nagios and other monitoring applications). Results include explainations and resolution steps.
* Configuration information
* Controler and Service statistics, including:
   * hits
   * average response time
   * cache usage
   * cache efficiency
   * functional and error ratio
   * hit rate. 
* Batch progress and history including failures and invalid objects. 
* Radiator-compatible screen for monitoring

## Why not use Metrics / Hawtio / another web console

AppStatus does not show raw technical counters. It tries to use runtime behavior and developpers hint to produce high value reports. Its API can be used to improve reporting and diagnostics.

The web console can be used by non technical users to ensure everything is working as expected with no possibility to break the application.

## Installation 

### Maven dependency 

pom.xml
```xml
<!-- Repository -->
<repository>
	<id>appstatus-repository</id>
	<name>App Status repository</name>
	<url>http://appstatus.sourceforge.net/maven2/repository</url>
</repository>

<!-- Optionally, you can also use snapshots -->
<repository>
	<id>appstatus-repository-snapshots</id>
	<name>App Status repository</name>
	<url>http://appstatus.sourceforge.net/maven2/snapshots</url>
</repository>


<!-- Performance / cache / error monitoring -->
<dependency>
	<groupId>net.sf.appstatus</groupId>
    	<artifactId>appstatus-services-inprocess</artifactId>
    	<version>0.7.0-SNAPSHOT</version>
    	<type>jar</type>
</dependency>

<!-- Batch / background tasks monitoring -->
<dependency>
    	<groupId>net.sf.appstatus</groupId>
    	<artifactId>appstatus-batch-inprocess</artifactId>
    	<version>0.7.0-SNAPSHOT</version>
</dependency>
    
<!-- Web console -->    
<dependency>
    	<groupId>net.sf.appstatus </groupId>
    	<artifactId>appstatus-web</artifactId>
    	<version>0.7.0-SNAPSHOT</version>
</dependency>
``` 


### With Spring 



WEB-INF/web.xml  (only for web console )
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

### Without Spring 

AppStatus can also be used without Spring : 

* Just add dependencies (services, batch), they will be detected and used.
* Configure with /status-check.properties
* AOP is not available in this configuration.

## Website and Wiki


* See website http://appstatus.sourceforge.net for additional details. 
* Wiki has some configuration examples https://github.com/appstatus/appstatus/wiki
