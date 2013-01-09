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

/**
 * Status check result.
 * 
 * @author Nicolas Richeton
 */
public interface ICheckResult extends Comparable<ICheckResult> {

	int ERROR = -1;
	int OK = 0;

	int getCode();

	String getDescription();

	String getGroup();

	String getProbeName();

	/**
	 * On error, should provide some advices on how to solve the issue. (Fix
	 * values in some property file, ensure remote service is running).
	 * 
	 * @return
	 */
	String getResolutionSteps();

	/**
	 * If true, the application status will be set to ERROR. If false the error
	 * will be considered as a warning.
	 * 
	 * @return
	 */
	boolean isFatal();

	void setDescription(String description);

	void setResolutionSteps(String resolutionSteps);
}
