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

import static net.sf.appstatus.check.impl.StatusResult.ERROR;
import net.sf.appstatus.annotations.AppCheckMethod;
import net.sf.appstatus.check.impl.AbstractHttpStatusChecker;
import net.sf.appstatus.check.impl.StatusResult;

public class DummyStatusChecker extends AbstractHttpStatusChecker {

  @AppCheckMethod
  public StatusResult checkStatus() {
    StatusResult result = new StatusResult();

    result.setProbeName(getName());
    double random = Math.random();
    if (random < 0.33) {
      result.setCode(OK);
    } else if (random > 0.66) {
      result.setCode(ERROR);
    } else {
      result.setCode(FATAL);
    }
    if (result.getCode() == OK) {
      result.setDescription("Random is good");
    } else {
      result.setDescription("Random failed");
      result.setResolutionSteps("This probe fails randomly. Please reload the page for better luck.");
    }
    return result;
  }

  public String getName() {
    return "Dummy check";
  }

}
