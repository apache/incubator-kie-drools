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
 * A simple client-side generator of fake email messages.
 */
public class RuleListData {

  private RuleListItem[] items;
	

  public RuleListData() {
	  
	  items = new RuleListItem[42];
	  
	  items[0] = new RuleListItem("MyCommunity 97%", "production", "mproctor");
	  items[1] = new RuleListItem("MyCommunity 100%", "production", "mproctor");
	  items[2] = new RuleListItem("Autum deals", "draft", "mneale");
	  items[3] = new RuleListItem("Alan parsons", "draft", "bmchirter");
	  
	  
	  for (int i = 4; i < items.length; i++) {
		items[i] = new RuleListItem("Dummy rule", "draft", "anon");
	  }
	  
  }
  
  
  public int getMailItemCount() {
    return items.length;
  }

  public RuleListItem getMailItem(int index) {
    if (index >= items.length)
      return null;
    return items[index];
  }

  
  
  
}
