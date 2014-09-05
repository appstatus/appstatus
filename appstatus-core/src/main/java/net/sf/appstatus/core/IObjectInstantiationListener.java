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
package net.sf.appstatus.core;

/**
 * Object instantiation listener.
 * 
 * <p>
 * This allows to delegate object creation to a custom class. Can be used to
 * create beans with spring instead of default Class#newInstance().
 * 
 * @author Nicolas Richeton
 * 
 */
public interface IObjectInstantiationListener {

	/**
	 * Try to instantiate the 'className' object. If object cannot be created,
	 * the AppStatus will try to create it by itself.
	 * 
	 * 
	 * @param className
	 * @return object instance or null.
	 */
	Object getInstance(String className);
}
