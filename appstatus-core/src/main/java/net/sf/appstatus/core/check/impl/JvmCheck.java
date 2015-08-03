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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import net.sf.appstatus.core.check.AbstractCheck;
import net.sf.appstatus.core.check.CheckResultBuilder;
import net.sf.appstatus.core.check.ICheckResult;

/**
 * @author Nicolas Richeton
 *
 */
public class JvmCheck extends AbstractCheck {

	public ICheckResult checkStatus() {
		MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
		MemoryUsage heap = memory.getHeapMemoryUsage();

		long heapRatio = heap.getUsed() * 100 / heap.getMax();
		CheckResultBuilder result = result(this);
		if (heapRatio > 95) {
			result.code(ICheckResult.ERROR)
			.fatal(true)
			.resolutionSteps(
					"Increase memory allocation (JVM arg -Xmx) or reduce memory usage in application code.");
		} else if (heapRatio > 80) {
			result.code(ICheckResult.ERROR)
			.fatal(false)
			.resolutionSteps(
					"Monitor closely memory usage and increase memory allocation (JVM arg -Xmx) if necessary");
		} else {
			result.code(ICheckResult.OK);
		}
		result.description("Using " + heapRatio + "% of maximum memory (Heap).");
		return result.build();
	}

	public String getGroup() {
		return "JVM";
	}

	public String getName() {
		return "Heap usage";
	}

}
