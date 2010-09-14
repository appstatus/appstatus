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

import net.sf.appstatus.IStatusChecker;
import net.sf.appstatus.IStatusResult;
import net.sf.appstatus.check.impl.StatusResultImpl;

/**
 * Basic service sample status checker.
 * 
 * @author Guillaume Mary
 * 
 */
public class ServiceSampleStatusChecker implements IStatusChecker {

	/**
	 * {@inheritDoc}
	 */
	public IStatusResult checkStatus() {
		StatusResultImpl result = new StatusResultImpl();
		result.setProbeName(getName());
		result.setDescription("Sample service status check");
		ServiceSample service = new ServiceSample();
		try {
			service.myService();
			if (System.currentTimeMillis() % 8 == 0) {
				throw new Exception("Random exception");
			}
		} catch (Exception e) {
			result.setFatal(true);
			result.setCode(IStatusResult.ERROR);
			result.setResolutionSteps("This is a random error, refresh the status.");
		}
		result.setCode(IStatusResult.OK);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return "Service Sample";
	}

}
