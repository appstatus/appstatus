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

import net.sf.appstatus.core.check.impl.StatusResultImpl;

public class CheckResultBuilder {

	int code;
	boolean fatal;
	String name, group, description, resolutionSteps;

	public CheckResultBuilder() {
	}

	public ICheckResult build() {
		StatusResultImpl result = new StatusResultImpl();
		result.setGroup(group);
		result.setProbeName(name);
		result.setCode(code);
		result.setFatal(code == ICheckResult.OK ? false : fatal);
		result.setDescription(description);
		result.setResolutionSteps(resolutionSteps);
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

	public CheckResultBuilder fatal(boolean fatal) {
		this.fatal = fatal;
		return this;
	}

	public CheckResultBuilder from(ICheck check) {
		this.name = check.getName();
		this.group = check.getGroup();
		return this;
	}

	public CheckResultBuilder resolutionSteps(String resolutionSteps) {
		this.resolutionSteps = resolutionSteps;
		return this;
	}

}
