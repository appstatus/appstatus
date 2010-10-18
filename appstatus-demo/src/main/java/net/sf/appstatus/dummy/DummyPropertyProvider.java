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
package net.sf.appstatus.dummy;

import java.util.HashMap;
import java.util.Map;

import net.sf.appstatus.IPropertyProvider;
import net.sf.appstatus.annotations.AppStatusProperties;

public class DummyPropertyProvider implements IPropertyProvider {

  public String getCategory() {
    return "Dummy";
  }

  @AppStatusProperties("Dummy")
  public Map<String, String> getMyProperties() {
    HashMap<String, String> hm = new HashMap<String, String>();
    hm.put("version", "1.0-demo");
    return hm;
  }

  public Map<String, String> getProperties() {
    HashMap<String, String> hm = new HashMap<String, String>();
    hm.put("version", "1.0-demo");
    return hm;
  }

}
