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
package net.sf.appstatus.samples.batch.classic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sf.appstatus.agent.batch.IJobProgressAgent;
import net.sf.appstatus.agent.batch.JobProgressAgentFactory;

/**
 * Classic sample batch.
 * 
 * @author Guillaume Mary
 * 
 */
public class BatchSample implements Runnable {

	/**
	 * Sample batch.
	 * 
	 * @param args
	 *            batch args
	 */
	public static void main(String[] args) {
		BatchSample batch = new BatchSample();
		batch.run();
	}

	/**
	 * Create item list.
	 * 
	 * @param stepMonitor
	 *            step monitor
	 * @return items
	 */
	private static List<String> step1(IJobProgressAgent stepMonitor) {
		stepMonitor.beginTask("step1", "SampleGroup", "Create the item list",
				100);
		List<String> items = new ArrayList<String>();
		String item = null;
		for (int i = 0; i < 100; i++) {
			item = "item" + i;
			stepMonitor.setCurrentItem(item);
			if (i % 5 == 0) {
				stepMonitor.reject(item, "Test the reject feature", null);
			} else {
				try {
					Thread.sleep(i * 100);
					items.add(item);
					stepMonitor.message(item + " item added");
				} catch (InterruptedException e) {
					stepMonitor.reject(item, e.getMessage(), null);
				}
			}
			stepMonitor.worked(1);
		}
		stepMonitor.done();
		return items;
	}

	/**
	 * Write the list content to the console.
	 * 
	 * @param items
	 *            items
	 * @param stepMonitor
	 *            step monitor
	 */
	private static void step2(List<String> items, IJobProgressAgent stepMonitor) {
		stepMonitor.beginTask("step2", "SampleGroup",
				"Write the items in the console output.", items.size());
		for (String item : items) {
			stepMonitor.message("Writing item : " + item);
			System.out.println(item);
			stepMonitor.worked(1);
		}
		stepMonitor.done();
	}

	public void run() {
		// retrieve the job monitor
		IJobProgressAgent jobMonitor = JobProgressAgentFactory.getAgent(UUID
				.randomUUID().toString());

		// start the job
		jobMonitor.beginTask("sample", "SampleGroup", "A batch sample", 2);

		// call step 1 (process 100 items)
		List<String> items = step1(jobMonitor.createSubTask(1));

		// call step 2
		step2(items, jobMonitor.createSubTask(1));

		// end the job
		jobMonitor.done();
	}
}
