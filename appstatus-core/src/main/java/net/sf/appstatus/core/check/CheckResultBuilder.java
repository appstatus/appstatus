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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import net.sf.appstatus.core.check.impl.StatusResultImpl;

public class CheckResultBuilder {

	int code;
	private Object[] descriptionArgs;
	boolean fatal = false;
	private Locale locale;
	String name, group, description, resolutionSteps, bundle = null;
	private Object[] resolutionStepArgs;

	public CheckResultBuilder() {
	}

	public ICheckResult build() {

		ResourceBundle resBundle = null;
		if (bundle != null) {
			resBundle = ResourceBundle.getBundle(bundle, locale);
		}

		StatusResultImpl result = new StatusResultImpl();

		result.setGroup(resBundle != null ? resBundle.getString(group) : group);
		result.setProbeName(resBundle != null ? resBundle.getString(name) : name);
		result.setCode(code);
		result.setFatal(code == ICheckResult.OK ? false : fatal);

		if ((resolutionStepArgs != null || descriptionArgs != null) && bundle == null) {
			throw new IllegalStateException("messageBundle() must be set when using messages with args.");
		}

		if (bundle != null) {
			result.setDescription(MessageFormat.format(resBundle.getString(description), descriptionArgs));
		} else {
			result.setDescription(description);
		}

		if (bundle != null) {
			result.setDescription(MessageFormat.format(resBundle.getString(resolutionSteps), resolutionStepArgs));
		} else {
			result.setResolutionSteps(resolutionSteps);
		}
		return result;
	}

	public CheckResultBuilder code(int code) {
		this.code = code;
		return this;
	}

	public CheckResultBuilder description(String description) {
		this.description = description;
		return this;
	}

	public CheckResultBuilder description(String description, Object... args) {
		this.description = description;
		this.descriptionArgs = args;
		return this;
	}

	public CheckResultBuilder fatal() {
		this.fatal = true;
		return this;
	}

	public CheckResultBuilder from(ICheck check) {
		this.name = check.getName();
		this.group = check.getGroup();
		return this;
	}

	/**
	 * Uses a message bundle to look for strings.
	 * <ul>
	 * <li>check name</li>
	 * <li>check group</li>
	 * <li>description</li>
	 * <li>resolutionSteps</li>
	 * </ul>
	 *
	 * @param bundle
	 * @param locale
	 * @return
	 */
	public CheckResultBuilder messageBundle(String bundle, Locale locale) {
		this.bundle = bundle;
		this.locale = locale;
		return this;
	}

	public CheckResultBuilder resolutionSteps(String resolutionSteps) {
		this.resolutionSteps = resolutionSteps;
		return this;
	}

	public CheckResultBuilder resolutionSteps(String resolutionSteps, Object... args) {
		this.resolutionSteps = resolutionSteps;
		this.resolutionStepArgs = args;
		return this;
	}

}
