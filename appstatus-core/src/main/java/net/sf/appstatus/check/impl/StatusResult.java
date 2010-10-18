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
package net.sf.appstatus.check.impl;

/**
 * Default Status check result.
 * 
 * @author Nicolas Richeton
 * 
 */
public class StatusResult {
  /**
   * Code for status ERROR.
   */
  public static final int ERROR = 1;
  /**
   * Code for status FATAL.
   */
  public static final int FATAL = -1;
  /**
   * code for status OK.
   */
  public static final int OK = 0;

  private int code;
  private String description = "";
  private String probeName = "unnamed";
  private String resolutionSteps = "";

  public StatusResult() {
    // empty constructor
  }

  public StatusResult(int code, String probeName, String description, String resolutionSteps) {
    this.code = code;
    this.probeName = probeName;
    this.description = description;
    this.resolutionSteps = resolutionSteps;
  }

  public int getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  public String getProbeName() {
    return probeName;
  }

  public String getResolutionSteps() {
    return resolutionSteps;
  }

  public boolean isFatal() {
    return this.code == FATAL;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setProbeName(String probeName) {
    this.probeName = probeName;
  }

  public void setResolutionSteps(String resolutionSteps) {
    this.resolutionSteps = resolutionSteps;
  }

}
