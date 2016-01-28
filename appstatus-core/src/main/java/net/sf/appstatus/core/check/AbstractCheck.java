/*
 * Copyright 2010 Capgemini and Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sf.appstatus.core.check;

import java.util.Locale;
import java.util.Properties;

import net.sf.appstatus.core.check.impl.StatusResultImpl;

/**
 * @author Nicolas Richeton
 *
 */
public abstract class AbstractCheck implements ICheck, IConfigurationAware {

	protected static final int FATAL = 2;
	protected static final int OK = 0;
	protected static final int WARN = 1;
	private Properties configuration;

	/**
	 * @deprecated use {@link #checkStatus(Locale)} instead.
	 * @return
	 */
	@Deprecated
	public ICheckResult checkStatus() {
		return null;
	}

	/**
	 * This method must be overriden and implemented by Checks.
	 * <p>
	 * Note: default implementation ensure compatibility with old check by
	 * calling {@link #checkStatus()}
	 *
	 * @param locale
	 *            locale to used for description and resolution steps.
	 *
	 * @return check result.
	 */
	public ICheckResult checkStatus(Locale locale) {
		return checkStatus();
	}

	/**
	 * Create result. Details can then be added using
	 * {@link ICheckResult#setDescription(String)} and
	 * {@link ICheckResult#setResolutionSteps(String)}.
	 *
	 * @deprecated use {@link CheckResultBuilder} instead.
	 * @param code
	 *            {@link AbstractCheck#OK} or {@link AbstractCheck#FATAL}
	 * @return ICheckResult object
	 */
	@Deprecated
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

	public Properties getConfiguration() {
		return configuration;
	}

	/**
	 * Create a generic result. name and group are NOT set and it's up to the
	 * caller to call {@link CheckResultBuilder#from(ICheck)}
	 *
	 * @return CheckResultBuilder to use.
	 */
	protected CheckResultBuilder result() {
		return new CheckResultBuilder();
	}

	/**
	 * Create a generic result.
	 *
	 * @param
	 *
	 * @return CheckResultBuilder to use.
	 */
	protected CheckResultBuilder result(ICheck check) {
		return new CheckResultBuilder().from(check);
	}

	public void setConfiguration(Properties configuration) {
		this.configuration = configuration;
	}
}
