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
package net.sf.appstatus.core.check.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.check.AbstractCheck;
import net.sf.appstatus.core.check.IAppStatusAware;
import net.sf.appstatus.core.check.ICheckResult;
import net.sf.appstatus.core.services.IService;

/**
 * @author Nicolas Richeton
 *
 */
public class ServicesPerformanceCheck extends AbstractCheck implements IAppStatusAware {

	private AppStatus appStatus;
	private final int limitError = 3000;
	private final int limitWarn = 1000;

	public ICheckResult checkStatus() {
		List<IService> services = appStatus.getServiceManager().getServices();
		List<String> warns = new ArrayList<String>();
		List<String> errors = new ArrayList<String>();

		for (IService s : services) {
			if (s.getAvgResponseTime() > limitError || s.getAvgResponseTimeWithCache() > limitError) {
				errors.add("Service " + s.getGroup() + "-" + s.getName() + " average response time ("
						+ s.getAvgResponseTime() + "/" + s.getAvgNestedCallsWithCache()
						+ "cached) is over error limit (" + limitError + ")");
			} else if (s.getAvgResponseTime() > limitWarn || s.getAvgResponseTimeWithCache() > limitWarn) {
				warns.add("Service " + s.getGroup() + "-" + s.getName() + " average response time ("
						+ s.getAvgResponseTime() + "/" + s.getAvgNestedCallsWithCache() + "cached) is over warn limit ("
						+ limitWarn + ")");
			}
		}

		ICheckResult result = null;
		if (errors.size() > 0) {

			result = result().code(ICheckResult.ERROR).fatal(true).description(StringUtils.join(errors, "<br/>"))
					.build();
		} else if (warns.size() > 0) {
			result = result().code(ICheckResult.ERROR).fatal(false).description(StringUtils.join(warns, "<br/>"))
					.build();
		} else {
			result = result().code(ICheckResult.OK).description("All average times under " + limitWarn + "ms").build();
		}
		return result;
	}

	public String getGroup() {
		return "Services";
	}

	public String getName() {
		return "Performance";
	}

	public void setAppStatus(AppStatus appStatus) {
		this.appStatus = appStatus;

	}

}
