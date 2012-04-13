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
package net.sf.appstatus.demo.check;

import net.sf.appstatus.core.check.ICheckResult;

public class GoogleHttpStatusChecker extends AbstractHttpCheck {

	public ICheckResult checkStatus() {
		ICheckResult result = null;

		try {
			this.doHttpGet("http://www.google.com");
			result = createResult(OK);
			result.setDescription("Google Access ok");
		} catch (Exception e) {
			result = createResult(FATAL);
			result.setDescription("Google access failed");
			result.setResolutionSteps("Your server does not have internet access : "
					+ e.getMessage());
		}

		return result;
	}

	public String getGroup() {
		return "google";
	}

	public String getName() {
		return "Google Http check";
	}

}
