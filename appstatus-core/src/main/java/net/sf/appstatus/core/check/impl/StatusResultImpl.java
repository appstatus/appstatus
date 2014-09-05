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
package net.sf.appstatus.core.check.impl;

import net.sf.appstatus.core.check.ICheckResult;

import org.apache.commons.lang3.ObjectUtils;

/**
 * Default Status check result.
 * 
 * @author Nicolas Richeton
 * 
 */
public class StatusResultImpl implements ICheckResult {
	private int code;
	private String description;
	private boolean fatal;
	private String group;
	private String probeName;
	private String resolutionSteps;

	public int compareTo(ICheckResult otherResult) {
		int groupCompare = ObjectUtils.compare(group, otherResult.getGroup());
		if (groupCompare != 0) {
			return groupCompare;
		}

		return ObjectUtils.compare(probeName, otherResult.getProbeName());
	}

	public int getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getGroup() {
		return group;
	}

	public String getProbeName() {
		return probeName;
	}

	public String getResolutionSteps() {
		return resolutionSteps;
	}

	public boolean isFatal() {
		return fatal;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFatal(boolean fatal) {
		this.fatal = fatal;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setProbeName(String probeName) {
		this.probeName = probeName;
	}

	public void setResolutionSteps(String resolutionSteps) {
		this.resolutionSteps = resolutionSteps;
	}
}
