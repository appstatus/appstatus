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

import net.sf.appstatus.core.AppStatus;
import net.sf.appstatus.core.batch.IBatchProgressMonitor;
import net.sf.appstatus.core.services.IServiceMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classic sample batch, using spring injection for appstatus.
 * 
 * @author Guillaume Mary
 * @author Nicolas Richeton
 * 
 */
public class BatchSample implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(BatchSample.class);

	private ServiceSample service;

	public void setService(ServiceSample service) {
		this.service = service;
	}

	/**
	 * Create item list.
	 * 
	 * @param stepMonitor
	 *            step monitor
	 * @return items
	 */
	private List<String> step1(IBatchProgressMonitor stepMonitor) {
		stepMonitor.beginTask("step1", "Create the item list", 100);
		List<String> items = new ArrayList<String>();
		String item = null;
		for (int i = 0; i < 100; i++) {
			IServiceMonitor monitor = appstatus.getServiceMonitor("Dummy service", "dummy");

			monitor.beginCall("item");
			item = "item" + i;
			monitor.endCall();
			stepMonitor.setCurrentItem(item);
			if (i % 5 == 0) {
				stepMonitor.reject(item, "Test the reject feature", null);
			} else {
				try {
					Thread.sleep(500);
					items.add(item);
					stepMonitor.message(item + " item added");
				} catch (InterruptedException e) {
					stepMonitor.reject(item, e.getMessage(), null);
				}
			}
			stepMonitor.worked(1);

			monitor = appstatus.getServiceMonitor("Refs service", "getRef");
			monitor.beginCall();
			service.getRefs();
			if (i % 10 == 0) {
				monitor.error("Test erreur reporting");}
			monitor.endCall();
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
	private void step2(List<String> items, IBatchProgressMonitor stepMonitor) {
		stepMonitor.beginTask("step2", "Write the items in the console output.", items.size());
		for (String item : items) {
IServiceMonitor sm =	appstatus.getServiceMonitor("Console Write", "Console");
			stepMonitor.message("Writing item : " + item);
			try {
				sm.beginCall(null);
				Thread.sleep(100);
			} catch (InterruptedException e) {
				sm.failure("", e);
				e.printStackTrace();
			} finally{
				sm.endCall();
			}
			stepMonitor.worked(1);
		}
		stepMonitor.done();
	}

	public void run() {

		// retrieve the job monitor
		IBatchProgressMonitor jobMonitor = appstatus.getBatchProgressMonitor("Sample job", "sample", UUID.randomUUID()
				.toString());

		jobMonitor.setLogger(logger);
		// start the job
		jobMonitor.beginTask("sample", "A batch sample", 2);

		// call step 1 (process 100 items)
		List<String> items = step1(jobMonitor.createSubTask(1));

		// call step 2
		step2(items, jobMonitor.createSubTask(1));

		// end the job
		if (System.currentTimeMillis() % 4 == 0) {
			jobMonitor.fail("Just to test failure");

		} else
			jobMonitor.done();
	}

	private AppStatus appstatus;

	public void setAppstatus(AppStatus appstatus) {
		this.appstatus = appstatus;
	}
}
