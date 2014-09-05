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
package net.sf.appstatus.demo.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sf.appstatus.core.AppStatusStatic;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;
import net.sf.appstatus.core.services.IServiceMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classic sample batch.
 * 
 * @author Guillaume Mary
 * 
 */
public class BatchSample implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(BatchSample.class);

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
	private static List<String> step1(IBatchProgressMonitor stepMonitor) {
		stepMonitor.beginTask("step1", "Create the item list", 100);
		List<String> items = new ArrayList<String>();
		String item = null;
		for (int i = 0; i < 100; i++) {
			IServiceMonitor serviceMonitor = AppStatusStatic.getInstance()
					.getServiceMonitor("Dummy service", "dummy");

			serviceMonitor.beginCall("item");
			item = "item" + i;
			stepMonitor.setCurrentItem(item);
			if (i % 5 == 0) {
				serviceMonitor.failure("Oups");
				stepMonitor.reject(item, "Test the reject feature");
			} else {
				try {
					Thread.sleep(500);
					items.add(item);
					stepMonitor.message(item + " item added");
				} catch (InterruptedException e) {
					stepMonitor.reject(item, e.getMessage());
				}
			}
			serviceMonitor.endCall();
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
	private static void step2(List<String> items,
			IBatchProgressMonitor stepMonitor) {
		stepMonitor.beginTask("step2",
				"Write the items in the console output.", items.size());
		for (String item : items) {
			IServiceMonitor sm = AppStatusStatic.getInstance()
					.getServiceMonitor("Console Write", "Console");
			stepMonitor.message("Writing item : " + item);
			sm.beginCall(null);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sm.endCall();
			stepMonitor.worked(1);
		}
		stepMonitor.done();
	}

	public void run() {
		// retrieve the job monitor
		IBatchProgressMonitor jobMonitor = AppStatusStatic.getInstance()
				.getBatchProgressMonitor("Sample job", "sample",
						UUID.randomUUID().toString());

		jobMonitor.setLogger(logger);
		// start the job
		jobMonitor.beginTask("sample", "A batch sample", 2);

		// call step 1 (process 100 items)
		List<String> items = step1(jobMonitor.createSubTask(1));

		// call step 2
		step2(items, jobMonitor.createSubTask(1));

		// end the job
		jobMonitor.done();
	}
}
