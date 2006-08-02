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
package org.drools.brms.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;



/**
 * Introduction page.
 */
public class Info extends JBRMSFeature {

  public static ComponentInfo init() {
    return new ComponentInfo("Info", "JBoss Rules Managment Console.") {
      public JBRMSFeature createInstance() {
        return new Info();
      }

	public Image getImage() {		
		return new Image("images/drools.gif");
	}
    };
  }

  public Info() {
	  	
    setWidget(new HTML(
      "<div class='infoProse'>"
        + "Welcome to the JBoss Rules Management System console."
        + "<p>Currently very much a WIP."
        + "</div>"
        + "<div>"
        + "<img src='images/drools_logo.png'"
        + "</div>"
        ,
      true));
  }

  public void onShow() {
  }
}
