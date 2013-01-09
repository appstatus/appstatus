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
package net.sf.appstatus.core.check;

import net.sf.appstatus.core.check.impl.StatusResultImpl;

/**
 * @author Nicolas Richeton
 * 
 */
public abstract class AbstractCheck implements ICheck {

	protected static final int FATAL = 2;
	protected static final int OK = 0;
	protected static final int WARN = 1;

	/**
	 * Create result. Details can then be added using
	 * {@link ICheckResult#setDescription(String)} and
	 * {@link ICheckResult#setResolutionSteps(String)}.
	 * 
	 * @param code
	 *            {@link AbstractCheck#OK} or {@link AbstractCheck#FATAL}
	 * @return ICheckResult object
	 */
	protected ICheckResult createResult(int code) {
		StatusResultImpl result = new StatusResultImpl();
		result.setProbeName(getName());
		result.setGroup(getGroup());

		switch (code) {
		case OK:
			result.setCode(ICheckResult.OK);
			result.setFatal(false);

			break;
		case FATAL:
			result.setFatal(true);
			result.setCode(ICheckResult.ERROR);
			break;
		default:
			// WARN
			result.setFatal(false);
			result.setCode(ICheckResult.ERROR);
			break;
		}

		return result;
	}
}
