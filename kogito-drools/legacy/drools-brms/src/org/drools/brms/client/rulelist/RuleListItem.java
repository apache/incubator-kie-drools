/*
 * Copyright 2006 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.brms.client.rulelist;

/**
 * A simple structure containing the an item in a list for a rule asset.
 */
public final class RuleListItem {

  public RuleListItem(String ruleName, String status, String changedBy) {
    this.name = ruleName;
    this.status = status;
    this.changedBy = changedBy;
    this.version = "1";
  }

  public String name;
  public String status;
  public String changedBy;
  public String version;

}
