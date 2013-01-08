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
package net.sf.appstatus.core.services;

/**
 * Service monitor
 * 
 * @author Guillaume Mary
 * @author Nicolas Richeton
 * 
 */
public interface IServiceMonitor {

	/**
	 * Notify of the beginning of a call to a service.
	 * 
	 * @param operationName
	 *            operation name
	 * @param parameters
	 *            operation parameters
	 * @return call id
	 */
	void beginCall(Object... parameters);

	/**
	 * Reports that a cache system was used instead of performing the actual
	 * call.
	 * 
	 */
	void cacheHit();

	/**
	 * Notify the end of a call to a service.
	 * 
	 */
	void endCall();

	/**
	 * Reports an error : the call has succeed but returns an error (with data).
	 * 
	 * @param message
	 */
	void error(String message);

	/**
	 * Reports a failure : the call has failed completely
	 * 
	 * @param reason
	 */
	void failure(String reason);

	/**
	 * Reports a failure : the call has failed completely with exception e.
	 * 
	 * @param reason
	 * @param e
	 */
	void failure(String reason, Exception e);
}
