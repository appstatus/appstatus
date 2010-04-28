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
package org.appstatus.jmx;

import java.util.List;
import java.util.Map;

import net.sf.appstatus.IStatusResult;
import net.sf.appstatus.StatusService;

public class AppStatusMBean {
	StatusService service = null;
	
	public AppStatusMBean() {
		service = new StatusService();
	}
	

	public Map<String, Map<String, String>> getProperties() {
		return service.getProperties();
	}

	public List<IStatusResult> getStatus() {
		return service.checkAll();
	}
}
