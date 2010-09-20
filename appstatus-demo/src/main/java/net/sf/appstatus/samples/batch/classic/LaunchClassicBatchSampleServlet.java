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

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sample service call servlet, used to generate some service usage.
 * 
 * @author Guillaume Mary
 * 
 */
public class LaunchClassicBatchSampleServlet extends HttpServlet {

	private static final String ENCODING = "UTF-8";

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -8830877354343317996L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// launch the batch
		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.execute(new BatchSample());

		ServletOutputStream os = resp.getOutputStream();

		os.write("<html><head".getBytes(ENCODING));
		os.write("<body>".getBytes(ENCODING));
		os.write("<h1>Ok</h1>".getBytes(ENCODING));
		os.write("</body></html>".getBytes(ENCODING));
	}
}
