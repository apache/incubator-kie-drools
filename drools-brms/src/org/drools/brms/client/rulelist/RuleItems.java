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

import java.util.Vector;

/**
 * A simple client-side generator of fake email messages.
 */
public class RuleItems {
	
  private static final String[] ruleNames = new String[]{
		    "MyCommunity 100%", "MyCommunity 97%", "December Promotion", "Flex Alt 97%"};
	
	
  private static final int NUM_ITEMS = ruleNames.length; 


  private static final String[] statuses = new String[]{
    "draft", "production", "production","pending"
    };

  private static final String[] changedBy = new String[]{
    "mproctor -[Sun, 23 Apr 2006 13:10:03 +0000]",
    "mneale -[Sun, 23 Apr 2006 13:10:03 +0000]",
    "bmcwhirter -[Sun, 23 Apr 2006 13:10:03 +0000]", "mproctor -[Sun, 23 Apr 2006 13:10:03 +0000]"
    };

  private static int senderIdx = 0, emailIdx = 0, subjectIdx = 0,fragmentIdx = 0;
  
  private static Vector items = new Vector();

  static {
    for (int i = 0; i < NUM_ITEMS; ++i)
      items.add(createFakeItem());
  }

  public static int getMailItemCount() {
    return items.size();
  }

  public static RuleItem getMailItem(int index) {
    if (index >= items.size())
      return null;
    return (RuleItem) items.get(index);
  }

  private static RuleItem createFakeItem() {
    String sender = ruleNames[senderIdx++];
    if (senderIdx == ruleNames.length)
      senderIdx = 0;

    String email = statuses[emailIdx++];
    if (emailIdx == statuses.length)
      emailIdx = 0;

    String subject = changedBy[subjectIdx++];
    if (subjectIdx == changedBy.length)
      subjectIdx = 0;

    String body = "";

    return new RuleItem(sender, email, subject);
  }
}
