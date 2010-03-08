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

import net.sf.appstatus.IStatusResult;
import net.sf.appstatus.StatusResultImpl;
import net.sf.appstatus.check.impl.AbstractHttpStatusChecker;

public class DummyStatusChecker extends AbstractHttpStatusChecker {

	public IStatusResult checkStatus() {

		StatusResultImpl result = new StatusResultImpl();

		result.setProbeName(getName());
		result.setCode(Math.random() > 0.5 ? IStatusResult.OK
				: IStatusResult.ERROR);
		if (result.getCode() == IStatusResult.OK) {
			result.setDescription("Random is good");
			result.setFatal(false);
		} else {
			result.setDescription("Random failed");
			result
					.setResolutionSteps("This probe fails randomly. Please reload the page for better luck.");
			result.setFatal(true);
		}
		return result;
	}

	public String getName() {
		return "Dummy check";
	}

}
