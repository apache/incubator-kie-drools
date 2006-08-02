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

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Demonstrates {"@link com.google.gwt.user.client.ui.TabPanel}.
 */
public class Admin extends JBRMSFeature {

  public static ComponentInfo init() {
    return new ComponentInfo("Admin",
      "Administer the repository - security and preferences configuration.") {
      public JBRMSFeature createInstance() {
        return new Admin();
      }

	public Image getImage() { 
		return new Image("images/config.png");
	}
    };
  }

  private TabPanel fTabs = new TabPanel();

  public Admin() {
    fTabs.add(createImage("rembrandt/TheReturnOfTheProdigalSon.jpg"), "Security");
    fTabs.add(createImage("rembrandt/TheReturnOfTheProdigalSon.jpg"), "Preferences");
    fTabs.selectTab(0);

    fTabs.setWidth("100%");
    fTabs.setHeight("100%");
    setWidget(fTabs);
  }

  public void onShow() {
  }

  private Widget createImage(String imageUrl) {
    Image image = new Image(imageUrl);
    image.setStyleName("ks-images-Image");

    VerticalPanel p = new VerticalPanel();
    p.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
    p.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
    p.add(image);
    return p;
  }
}
