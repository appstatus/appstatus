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

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.check.AbstractCheck;
import net.sf.appstatus.core.check.IAppStatusAware;
import net.sf.appstatus.core.check.ICheckResult;
import net.sf.appstatus.core.services.IService;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Nicolas Richeton
 *
 */
public class ServicesFailureCheck extends AbstractCheck implements IAppStatusAware {

	private AppStatus appStatus;
	private int limitError = 10;
	private int limitWarn = 5;

	@Override
	public ICheckResult checkStatus() {
		List<IService> services = appStatus.getServiceManager().getServices();
		List<String> warns = new ArrayList<String>();
		List<String> errors = new ArrayList<String>();

		for (IService s : services) {
			if (s.getHits() == 0) {
				continue;
			}

			long failureRatio = (s.getFailures() * 100) / s.getHits();
			if (failureRatio > limitError) {
				errors.add("Service " + s.getGroup() + "-" + s.getName() + " failure ratio (" + failureRatio
						+ ") is over error limit (" + limitError + ")");
			} else if (failureRatio > limitWarn) {
				errors.add("Service " + s.getGroup() + "-" + s.getName() + " failure ratio (" + failureRatio
						+ ") is over error limit (" + limitWarn + ")");
			}
		}

		ICheckResult result = null;
		if (errors.size() > 0) {
			result = result(this)
					.code(ICheckResult.ERROR)
					.description(
							StringUtils.join(errors, "<br/>") + " <br/>Additional warnings: "
									+ StringUtils.join(warns, "<br/>")).build();
		} else if (warns.size() > 0) {
			result = result(this).code(ICheckResult.ERROR).description(StringUtils.join(warns, "<br/>")).build();
		} else {
			result = result(this).code(ICheckResult.OK).description("All failure ratios under " + limitWarn + "%")
					.build();
		}
		return result;
	}

	public String getGroup() {
		return "Services";
	}

	public String getName() {
		return "Failures";
	}

	public void setAppStatus(AppStatus appStatus) {
		this.appStatus = appStatus;

	}

	public void setLimitError(int limitError) {
		this.limitError = limitError;
	}

	public void setLimitWarn(int limitWarn) {
		this.limitWarn = limitWarn;
	}

}
