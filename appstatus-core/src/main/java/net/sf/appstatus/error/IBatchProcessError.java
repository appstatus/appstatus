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
package net.sf.appstatus.error;

/**
 * Define the informations needed to analyse a batch error.
 * 
 * @author Guillaume Mary
 * 
 */
public interface IBatchProcessError {

	/**
	 * Debug level error.
	 */
	public static final int DEBUG = 0;

	/**
	 * Error level error.
	 */
	public static final int ERROR = 3;

	/**
	 * Fatal level error.
	 */
	public static final int FATAL = 4;

	/**
	 * Info level error.
	 */
	public static final int INFO = 1;

	/**
	 * Warn level error.
	 */
	public static final int WARN = 2;

	/**
	 * Get the error root cause.
	 * 
	 * @return error cause
	 */
	Throwable getCause();

	/**
	 * Get the error code.
	 * 
	 * @return error code
	 */
	String getCode();

	/**
	 * Get the error description
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * Get the error item
	 * 
	 * @return error item
	 */
	Object getItem();

	/**
	 * Get the error level
	 * 
	 * @return error level
	 */
	int getLevel();

	/**
	 * Get the resolution steps.
	 * 
	 * @return resolutions steps
	 */
	String getResolutionSteps();

}
