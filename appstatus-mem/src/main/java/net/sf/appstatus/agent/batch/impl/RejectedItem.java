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
package net.sf.appstatus.agent.batch.impl;

import java.io.Serializable;

/**
 * Rejected item data.
 * 
 * @author Guillaume Mary
 * 
 */
public class RejectedItem implements Serializable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -5864358565730814945L;

	private String id;

	private Object item;

	private Throwable cause;

	private String reason;

	public RejectedItem(String id, Object item, String reason) {
		this.id = id;
		this.item = item;
		this.reason = reason;
	}

	public RejectedItem(String id, Object item, String reason, Throwable cause) {
		this.id = id;
		this.item = item;
		this.reason = reason;
		this.cause = cause;
	}

	public Throwable getCause() {
		return cause;
	}

	public String getId() {
		return id;
	}

	public Object getItem() {
		return item;
	}

	public String getReason() {
		return reason;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setItem(Object item) {
		this.item = item;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
