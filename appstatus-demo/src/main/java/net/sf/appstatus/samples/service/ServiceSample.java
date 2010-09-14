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

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample service.
 * 
 * @author Guillaume Mary
 * 
 */
public class ServiceSample {

	Logger log = LoggerFactory.getLogger(ServiceSample.class);

	public String anotherService(String firstParam, boolean secondParam) {
		return "ok";
	}

	public int myService() {
		sleep();
		return 0;
	}

	private void sleep() {
		Random rd = new Random();
		int waitDuration = rd.nextInt(100);
		try {
			Thread.sleep(waitDuration);
		} catch (InterruptedException e) {
			log.info("Exception during the sleep operation.", e);
		}
	}
}
