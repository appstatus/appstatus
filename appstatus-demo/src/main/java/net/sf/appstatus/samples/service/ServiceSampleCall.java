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
package net.sf.appstatus.samples.service;

import net.sf.appstatus.agent.service.IServiceAgent;
import net.sf.appstatus.agent.service.ServiceAgentFactory;

/**
 * Main sample service call class.
 * 
 * @author Guillaume Mary
 * 
 */
public class ServiceSampleCall implements Runnable {

	/**
	 * Sample service call.
	 * 
	 * @param args
	 *            no args
	 */
	public static void main(String[] args) {
		ServiceSampleCall sampleCall = new ServiceSampleCall();
		sampleCall.run();
	}

	private void generateServiceCall() {
		IServiceAgent monitor = ServiceAgentFactory
				.getAgent(ServiceSample.class.getName());
		ServiceSample service = new ServiceSample();
		String id = monitor.beginCall("myService", null);
		// call the service
		service.myService();
		monitor.endCall("myService", id);
	}

	public void run() {
		for (int i = 0; i < 100; i++) {
			generateServiceCall();
		}
	}
}
