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
package net.sf.appstatus.dummy;

import java.net.InetAddress;

import net.sf.appstatus.IStatusChecker;
import net.sf.appstatus.IStatusResult;
import net.sf.appstatus.StatusResultImpl;

public class GooglePingStatusChecker implements IStatusChecker {

	public IStatusResult checkStatus() {

		StatusResultImpl result = new StatusResultImpl();
		result.setProbeName(getName());

		try {
			InetAddress address = InetAddress.getByName("www.google.com");

			if (address.isReachable(2000)) {
				result.setDescription("Google Access ok");
				result.setCode(IStatusResult.OK);
			}else {
				throw new Exception( "Ping timeout (2000ms)");
			}

		} catch (Exception e) {
			result.setCode(IStatusResult.ERROR);
			result.setDescription("Google ping failed");
			result
					.setResolutionSteps("Ping failed. This means that ICMP messages are blocked by this host. (This may not be an issue) "
							+ e.getMessage());
			result.setFatal(false);
		}

		return result;
	}

	public String getName() {
		return "Google Ping check";
	}

}
