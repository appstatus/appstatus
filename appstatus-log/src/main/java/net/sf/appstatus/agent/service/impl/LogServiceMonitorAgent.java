package net.sf.appstatus.agent.service.impl;

import java.util.UUID;

import net.sf.appstatus.agent.service.IServiceMonitorAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogServiceMonitorAgent implements IServiceMonitorAgent {

	private static Logger log = LoggerFactory
			.getLogger(LogServiceMonitorAgent.class);

	private String executionId;

	private String serviceName;

	public String beginCall(String serviceName, Object[] parameters) {
		this.executionId = UUID.randomUUID().toString();
		log.info(
				"Debut de l'appel du service <{}({})>, avec les parametres : {}",
				new Object[] { serviceName, executionId, parameters });
		return executionId;
	}

	public void endCall(String id) {
		log.info("Fin de l'appel du service <{}({})>", new Object[] {
				serviceName, executionId });
	}

}
