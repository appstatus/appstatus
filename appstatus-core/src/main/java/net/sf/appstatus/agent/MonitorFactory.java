/*
 * Copyright 2010 Capgemini
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 */
package net.sf.appstatus.agent;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.sf.appstatus.agent.batch.IBatchExecutionMonitor;
import net.sf.appstatus.agent.batch.IBatchExecutionMonitorAgent;
import net.sf.appstatus.agent.batch.helpers.NOPBatchExecutionMonitor;
import net.sf.appstatus.agent.batch.helpers.NOPBatchExecutionMonitorAgent;
import net.sf.appstatus.agent.service.IServiceStatisticsMonitor;
import net.sf.appstatus.agent.service.IServiceStatisticsMonitorAgent;
import net.sf.appstatus.agent.service.helpers.NOPServiceStatisticsMonitor;
import net.sf.appstatus.agent.service.helpers.NOPServiceStatisticsMonitorAgent;
import net.sf.appstatus.monitor.Monitor;
import net.sf.appstatus.monitor.MonitorsPlugin;
import net.sf.appstatus.monitor.ResourceType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global Monitor Factory which provide monitor and associated agent.
 * 
 * @author Guillaume Mary
 * 
 * @param <T>
 *            Interface returned by the factory.
 */
public class MonitorFactory<T> {
	private static final String CONFIG_LOCATION = "monitors-plugin.xml";

	public static final String DEFAULT_MONITOR_NAME = "default_monitor_name";

	private static Logger logger = LoggerFactory
			.getLogger(MonitorFactory.class);

	private static Map<String, Map<String, Monitor>> confMonitorsMapByType = null;

	private static Map<String, IMonitor> monitorMap = new ConcurrentHashMap<String, IMonitor>();

	private static Map<String, IMonitorAgent> agentMap = new ConcurrentHashMap<String, IMonitorAgent>();

	/**
	 * Retrieve an already configured agent or create a new one
	 * 
	 * @param <T>
	 *            implementation of the IMonitorAgent interface
	 * @param type
	 *            interface of the returned implementation
	 * @param monitorName
	 *            name of the monitor implementation (ex:log,mem)
	 * @param params
	 *            parameters used to call
	 * @return instance of the specified or default implementation of the agent.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IMonitorAgent> T getAgent(
			Class<? extends IMonitorAgent> type, String monitorName,
			Object... params) {
		if (confMonitorsMapByType == null) {
			init();
		}
		String paramKey = ToStringBuilder.reflectionToString(params,
				ToStringStyle.SHORT_PREFIX_STYLE);
		String key = type.getName() + "." + monitorName + "." + paramKey;
		IMonitorAgent agent = agentMap.get(key);
		if (agent == null) {
			agent = newAgentInstance(type, monitorName, params);
			agentMap.put(key, agent);
		}
		return (T) agent;
	}

	/**
	 * Retrieve an already configured monitor or create a new one.
	 * 
	 * @param <T>
	 *            implementation of the IMonitor interface
	 * @param type
	 *            interface of the returned implementation
	 * @param monitorName
	 *            name of the monitor implementation (ex:log,mem)
	 * @param params
	 *            parameters used to call
	 * @return instance of the specified or default implementation of the
	 *         monitor.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IMonitor> T getMonitor(Class<T> type,
			String monitorName, Object... params) {
		if (confMonitorsMapByType == null) {
			init();
		}
		String paramKey = ToStringBuilder.reflectionToString(params,
				ToStringStyle.SHORT_PREFIX_STYLE);
		String key = type.getName() + "." + monitorName + "." + paramKey;
		IMonitor monitor = monitorMap.get(key);
		if (monitor == null) {
			monitor = newMonitorInstance(type, monitorName, params);
			monitorMap.put(key, monitor);
		}
		return (T) monitor;
	}

	private static synchronized void init() {
		try {
			Enumeration<URL> configFiles;
			// Load and init all probes
			ClassLoader classLoader = MonitorFactory.class.getClassLoader();
			if (classLoader == null) {
				configFiles = ClassLoader.getSystemResources(CONFIG_LOCATION);
			} else {
				configFiles = classLoader.getResources(CONFIG_LOCATION);
			}

			URL url = null;
			confMonitorsMapByType = new HashMap<String, Map<String, Monitor>>();
			JAXBContext jc = JAXBContext
					.newInstance("net.sf.appstatus.monitor");
			Unmarshaller u = jc.createUnmarshaller();
			while (configFiles.hasMoreElements()) {
				url = configFiles.nextElement();

				// Load plugin configuration
				MonitorsPlugin o = (MonitorsPlugin) u.unmarshal(url);
				for (Monitor monitor : o.getMonitor()) {
					if (confMonitorsMapByType.containsKey(monitor.getType()
							.name())) {
						Map<String, Monitor> confMonitorsMapByName = confMonitorsMapByType
								.get(monitor.getType());
						confMonitorsMapByName.put(monitor.getName(), monitor);
						confMonitorsMapByType.put(monitor.getType().name(),
								confMonitorsMapByName);
						logger.warn(
								"There is multiple implementation of the monitor : {}, the implementation used should be the first found in the classpath : {}",
								monitor.getType(), confMonitorsMapByName
										.keySet().iterator().next());
						logger.info(
								"New registered monitor :{}-{}({}) with the agent : {}",
								new Object[] { monitor.getType(),
										monitor.getName(), monitor.getClazz(),
										monitor.getAgent().getClazz() });
					} else {
						// add the new monitor
						Map<String, Monitor> confMonitorsMapByName = new HashMap<String, Monitor>();
						confMonitorsMapByName.put(monitor.getName(), monitor);
						confMonitorsMapByType.put(monitor.getType().name(),
								confMonitorsMapByName);
						logger.info(
								"New registered monitor :{}-{}({}) with the agent : {}",
								new Object[] { monitor.getType(),
										monitor.getName(), monitor.getClazz(),
										monitor.getAgent().getClazz() });
					}
				}
			}
		} catch (Exception e) {
			logger.error("Initialization error", e);
		}

	}

	private static IMonitorAgent newAgentInstance(
			Class<? extends IMonitorAgent> type, String monitorName,
			Object[] params) {
		IMonitorAgent agent = null;
		if (IServiceStatisticsMonitorAgent.class.isAssignableFrom(type)) {
			agent = newServiceAgentInstance(monitorName, params);
		} else if (IBatchExecutionMonitorAgent.class.isAssignableFrom(type)) {
			agent = newBatchAgentInstance(monitorName, params);
		}
		return agent;
	}

	private static IBatchExecutionMonitorAgent newBatchAgentInstance(String monitorName,
			Object[] params) {
		String className = null;
		Map<String, Monitor> confMonitorsByName = confMonitorsMapByType
				.get(ResourceType.BATCH.name());
		if (confMonitorsByName != null && !confMonitorsByName.isEmpty()) {
			if (monitorName.equals(DEFAULT_MONITOR_NAME)) {
				// retrieve the first agent
				className = confMonitorsByName.values().iterator().next()
						.getAgent().getClazz();
			} else if (confMonitorsByName.containsKey(monitorName)) {
				// retireve the selected agent
				className = confMonitorsByName.get(monitorName).getAgent()
						.getClazz();
			} else {
				logger.error(
						"The specified agent implementation : {} is not registered in the factory. Returning a NOP Agent.",
						monitorName);
				return NOPBatchExecutionMonitorAgent.NOP_BATCH_AGENT;
			}
		} else {
			return NOPBatchExecutionMonitorAgent.NOP_BATCH_AGENT;
		}
		// instanciation
		IBatchExecutionMonitorAgent agent = null;
		Class<?>[] constructorParamsTypes = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++) {
			constructorParamsTypes[i] = params[i].getClass();
		}
		try {
			agent = (IBatchExecutionMonitorAgent) Class.forName(className)
					.getConstructor(constructorParamsTypes).newInstance(params);
		} catch (Exception e) {
			logger.error(
					"Error during the initialisation of a service monitor agent class : {}, params : {}, cause : {}",
					new Object[] { className, params, e });
			logger.info("Return a NOP agent");
			agent = NOPBatchExecutionMonitorAgent.NOP_BATCH_AGENT;
		}
		return agent;
	}

	private static IMonitor newBatchMonitorInstance(String monitorName,
			Object[] params) {
		String className = null;
		Map<String, Monitor> confMonitorsByName = confMonitorsMapByType
				.get(ResourceType.BATCH.name());
		if (confMonitorsByName != null && !confMonitorsByName.isEmpty()) {
			if (monitorName.equals(DEFAULT_MONITOR_NAME)) {
				// retrieve the first monitor
				className = confMonitorsByName.values().iterator().next()
						.getClazz();
			} else if (confMonitorsByName.containsKey(monitorName)) {
				// retireve the selected agent
				className = confMonitorsByName.get(monitorName).getClazz();
			} else {
				logger.error(
						"The specified monitor implementation : {} is not registered in the factory. Returning a NOP Monitor.",
						monitorName);
				return NOPBatchExecutionMonitor.NOP_BATCH_MONITOR;
			}
		} else {
			return NOPBatchExecutionMonitor.NOP_BATCH_MONITOR;
		}
		// instanciation
		IBatchExecutionMonitor monitor = null;
		Class<?>[] constructorParamsTypes = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++) {
			constructorParamsTypes[i] = params[i].getClass();
		}
		try {
			monitor = (IBatchExecutionMonitor) Class.forName(className)
					.getConstructor(constructorParamsTypes).newInstance(params);
		} catch (Exception e) {
			logger.error(
					"Error during the initialisation of a service monitor class : {}, params : {}, cause : {}",
					new Object[] { className, params, e });
			logger.info("Return a NOP monitor");
			monitor = NOPBatchExecutionMonitor.NOP_BATCH_MONITOR;
		}
		return monitor;
	}

	private static IMonitor newMonitorInstance(Class<? extends IMonitor> type,
			String monitorName, Object[] params) {
		IMonitor monitor = null;
		if (IServiceStatisticsMonitor.class.isAssignableFrom(type)) {
			monitor = newServiceMonitorInstance(monitorName, params);
		} else if (IBatchExecutionMonitor.class.isAssignableFrom(type)) {
			monitor = newBatchMonitorInstance(monitorName, params);
		}
		return monitor;
	}

	private static IServiceStatisticsMonitorAgent newServiceAgentInstance(
			String monitorName, Object[] params) {
		String className = null;
		Map<String, Monitor> confMonitorsByName = confMonitorsMapByType
				.get(ResourceType.SERVICE.name());
		if (confMonitorsByName != null && !confMonitorsByName.isEmpty()) {
			if (monitorName.equals(DEFAULT_MONITOR_NAME)) {
				// retrieve the first agent
				className = confMonitorsByName.values().iterator().next()
						.getAgent().getClazz();
			} else if (confMonitorsByName.containsKey(monitorName)) {
				// retireve the selected agent
				className = confMonitorsByName.get(monitorName).getAgent()
						.getClazz();
			} else {
				logger.error(
						"The specified agent implementation : {} is not registered in the factory. Returning a NOP Agent.",
						monitorName);
				return NOPServiceStatisticsMonitorAgent.NOP_SERVICE_AGENT;
			}
		} else {
			return NOPServiceStatisticsMonitorAgent.NOP_SERVICE_AGENT;
		}
		// instanciation
		IServiceStatisticsMonitorAgent agent = null;
		Class<?>[] constructorParamsTypes = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++) {
			constructorParamsTypes[i] = params[i].getClass();
		}
		try {
			agent = (IServiceStatisticsMonitorAgent) Class.forName(className)
					.getConstructor(constructorParamsTypes).newInstance(params);
		} catch (Exception e) {
			logger.error(
					"Error during the initialisation of a service monitor agent class : {}, params : {}, cause : {}",
					new Object[] { className, params, e });
			logger.info("Return a NOP agent");
			agent = NOPServiceStatisticsMonitorAgent.NOP_SERVICE_AGENT;
		}
		return agent;
	}

	private static IMonitor newServiceMonitorInstance(String monitorName,
			Object[] params) {
		String className = null;
		Map<String, Monitor> confMonitorsByName = confMonitorsMapByType
				.get(ResourceType.SERVICE.name());
		if (confMonitorsByName != null && !confMonitorsByName.isEmpty()) {
			if (monitorName.equals(DEFAULT_MONITOR_NAME)) {
				// retrieve the first monitor
				className = confMonitorsByName.values().iterator().next()
						.getClazz();
			} else if (confMonitorsByName.containsKey(monitorName)) {
				// retireve the selected agent
				className = confMonitorsByName.get(monitorName).getClazz();
			} else {
				logger.error(
						"The specified monitor implementation : {} is not registered in the factory. Returning a NOP Monitor.",
						monitorName);
				return NOPServiceStatisticsMonitor.NOP_SERVICE_AGENT_MONITOR;
			}
		} else {
			return NOPServiceStatisticsMonitor.NOP_SERVICE_AGENT_MONITOR;
		}
		// instanciation
		IServiceStatisticsMonitor monitor = null;
		Class<?>[] constructorParamsTypes = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++) {
			constructorParamsTypes[i] = params[i].getClass();
		}
		try {
			monitor = (IServiceStatisticsMonitor) Class.forName(className)
					.getConstructor(constructorParamsTypes).newInstance(params);
		} catch (Exception e) {
			logger.error(
					"Error during the initialisation of a service monitor class : {}, params : {}, cause : {}",
					new Object[] { className, params, e });
			logger.info("Return a NOP monitor");
			monitor = NOPServiceStatisticsMonitor.NOP_SERVICE_AGENT_MONITOR;
		}
		return monitor;
	}

}
