/*
 * Copyright 2010-2013 Capgemini Licensed under the Apache License, Version 2.0 (the
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
package net.sf.appstatus.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This interface is implemented by every page of the web interface.
 * 
 * @author Nicolas Richeton
 * 
 */
public interface IPage {

	/**
	 * Process GET requests. Usually display current status.
	 * 
	 * @param webHandler
	 * @param req
	 * @param resp
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	void doGet(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp)
			throws UnsupportedEncodingException, IOException;

	/**
	 * Process POST requests. Usually for admin actions.
	 * 
	 * @param webHandler
	 * @param req
	 * @param resp
	 */
	void doPost(StatusWebHandler webHandler, HttpServletRequest req, HttpServletResponse resp);

	/**
	 * Id of this page.
	 * 
	 * @return
	 */
	String getId();

	/**
	 * Returns page name, used in url to trigger page rendering.
	 * 
	 * @return
	 */
	String getName();
}
