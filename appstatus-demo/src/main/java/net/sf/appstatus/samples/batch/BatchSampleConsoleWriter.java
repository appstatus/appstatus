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
package net.sf.appstatus.samples.batch;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

/**
 * Write received data
 * 
 * @author VPENET
 * 
 */

public class BatchSampleConsoleWriter implements ItemWriter<BatchSample> {
	/**
	 * Write data in console
	 * 
	 * @param List
	 *            of sample data
	 * 
	 */
	public void write(List<? extends BatchSample> samples) throws Exception {
		for (BatchSample sample : samples) {
			System.out.println(sample.toString());
		}
	}

}
