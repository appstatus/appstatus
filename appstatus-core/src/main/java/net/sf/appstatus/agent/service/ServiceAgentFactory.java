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
package net.sf.appstatus.agent.service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import net.sf.appstatus.agent.service.helpers.NOPServiceAgentFactory;
import net.sf.appstatus.agent.service.impl.StaticServiceAgentFactoryBinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service Agent factory.
 * 
 * @author Guillaume Mary
 * 
 */
public final class ServiceAgentFactory {

	static final int UNINITIALIZED = 0;
	static final int ONGOING_INITILIZATION = 1;
	static final int FAILED_INITILIZATION = 2;
	static final int SUCCESSFUL_INITILIZATION = 3;
	static final int NOP_FALLBACK_INITILIZATION = 4;
	static int INITIALIZATION_STATE = UNINITIALIZED;

	static NOPServiceAgentFactory NOP_FALLBACK_FACTORY = new NOPServiceAgentFactory();
	static NOPServiceAgentFactory TEMP_FACTORY = new NOPServiceAgentFactory();

	private static String STATIC_SERVICE_AGENT_FACTORY_BINDER_PATH = "net/sf/appstatus/agent/service/impl/StaticServiceAgentFactoryBinder.class";

	static final String UNSUCCESSFUL_INIT_MSG = "net.sf.appstatus.agent.service.ServiceAgentFactory could not be successfully initialized.";

	private static Logger log = LoggerFactory
			.getLogger(ServiceAgentFactory.class);

	/**
	 * Bind the service monitor factory.
	 */
	private final static void bind() {
		try {
			// the next line does the binding
			StaticServiceAgentFactoryBinder.getSingleton();
			INITIALIZATION_STATE = SUCCESSFUL_INITILIZATION;
		} catch (NoClassDefFoundError ncde) {
			String msg = ncde.getMessage();
			if (msg != null
					&& msg.indexOf("net/sf/appstatus/agent/service/impl/StaticServiceAgentFactoryBinder") != -1) {
				INITIALIZATION_STATE = NOP_FALLBACK_INITILIZATION;
				log.info("Failed to load class \"net.sf.appstatus.agent.service.impl.StaticServiceAgentFactoryBinder\".");
				log.info("Defaulting to no-operation (NOP) logger implementation");
			} else {
				failedBinding(ncde);
				throw ncde;
			}
		} catch (java.lang.NoSuchMethodError nsme) {
			String msg = nsme.getMessage();
			if (msg != null
					&& msg.indexOf("net.sf.appstatus.agent.service.impl.StaticServiceAgentFactoryBinder.getSingleton()") != -1) {
				INITIALIZATION_STATE = FAILED_INITILIZATION;
				log.error("appstatus-core is incompatible with this binding.");
				log.error("Upgrade your binding");
			}
			throw nsme;
		} catch (Exception e) {
			failedBinding(e);
			throw new IllegalStateException(
					"Unexpected initialization failure", e);
		}
	}

	/**
	 * Binding fail.
	 * 
	 * @param t
	 *            cause
	 */
	static void failedBinding(Throwable t) {
		INITIALIZATION_STATE = FAILED_INITILIZATION;
		log.error("Failed to instantiate Service Agent Factory", t);
	}

	/**
	 * Return a new {@link IServiceAgent} instance.
	 * 
	 * @return a new {@link IServiceAgent} instance
	 */
	public static IServiceAgent getAgent(String serviceName) {
		IServiceAgentFactory agentFactory = getIServiceAgentFactory();
		return agentFactory.getAgent(serviceName);
	}

	/**
	 * Return the {@link IServiceAgentFactory} instance in use.
	 * 
	 * <p>
	 * IServiceAgentFactory instance is bound with this class at compile time.
	 * 
	 * @return the IServiceAgentFactory instance in use
	 */
	public static IServiceAgentFactory getIServiceAgentFactory() {
		if (INITIALIZATION_STATE == UNINITIALIZED) {
			INITIALIZATION_STATE = ONGOING_INITILIZATION;
			performInitialization();

		}
		switch (INITIALIZATION_STATE) {
		case SUCCESSFUL_INITILIZATION:
			return StaticServiceAgentFactoryBinder.getSingleton()
					.getServiceAgentFactory();
		case NOP_FALLBACK_INITILIZATION:
			return NOP_FALLBACK_FACTORY;
		case FAILED_INITILIZATION:
			throw new IllegalStateException(UNSUCCESSFUL_INIT_MSG);
		case ONGOING_INITILIZATION:
			return TEMP_FACTORY;
		}
		throw new IllegalStateException("Unreachable code");
	}

	private final static void performInitialization() {
		singleImplementationSanityCheck();
		bind();
	}

	/**
	 * Check if the implementation is unique.
	 */
	private static void singleImplementationSanityCheck() {
		try {
			ClassLoader serviceAgentFactoryClassLoader = ServiceAgentFactory.class
					.getClassLoader();
			Enumeration<URL> paths;
			if (serviceAgentFactoryClassLoader == null) {
				paths = ClassLoader
						.getSystemResources(STATIC_SERVICE_AGENT_FACTORY_BINDER_PATH);
			} else {
				paths = serviceAgentFactoryClassLoader
						.getResources(STATIC_SERVICE_AGENT_FACTORY_BINDER_PATH);
			}
			List<URL> implementationList = new ArrayList<URL>();
			while (paths.hasMoreElements()) {
				URL path = paths.nextElement();
				implementationList.add(path);
			}
			if (implementationList.size() > 1) {
				log.error("Class path contains multiple Service Agent bindings.");
				for (int i = 0; i < implementationList.size(); i++) {
					log.error("Found binding in [" + implementationList.get(i)
							+ "]");
				}
			}
		} catch (IOException ioe) {
			log.error("Error getting resources from path", ioe);
		}
	}

	/**
	 * Default constructor.
	 */
	private ServiceAgentFactory() {
		// prevent instantiation
	}

}
