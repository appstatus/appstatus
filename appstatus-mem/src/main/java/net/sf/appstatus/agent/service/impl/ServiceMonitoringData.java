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
package net.sf.appstatus.agent.service.impl;

import java.io.Serializable;

/**
 * Data store during a service call.
 * @author Guillaume Mary
 *
 */
public class ServiceMonitoringData implements Serializable {
	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 5285426440662998057L;

	private long startCallTimestamp;
	
	private long endCallTimestamp;

	public void setStartCallTimestamp(long startCallTimestamp) {
		this.startCallTimestamp = startCallTimestamp;
	}

	public long getStartCallTimestamp() {
		return startCallTimestamp;
	}

	public void setEndCallTimestamp(long endCallTimestamp) {
		this.endCallTimestamp = endCallTimestamp;
	}

	public long getEndCallTimestamp() {
		return endCallTimestamp;
	}
}
