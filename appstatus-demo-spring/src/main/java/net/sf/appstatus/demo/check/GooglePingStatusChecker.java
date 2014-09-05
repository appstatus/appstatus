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

import java.net.InetAddress;

import net.sf.appstatus.core.check.AbstractCheck;
import net.sf.appstatus.core.check.ICheckResult;

public class GooglePingStatusChecker extends AbstractCheck {

	public ICheckResult checkStatus() {
		ICheckResult result = null;

		try {
			InetAddress address = InetAddress.getByName("www.google.com");

			if (address.isReachable(2000)) {
				result = createResult(OK);
				result.setDescription("Google Access ok");

			} else {
				throw new Exception("Ping timeout (2000ms)");
			}

		} catch (Exception e) {
			result = createResult(WARN);
			result.setDescription("Google ping failed");
			result.setResolutionSteps("Ping failed. This means that ICMP messages are blocked by this host. (This may not be an issue) "
					+ e.getMessage());

		}

		return result;
	}

	public String getGroup() {
		return "google";
	}

	public String getName() {
		return "Google Ping check";
	}

}
