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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * Demonstrates {@link com.google.gwt.user.client.ui.MenuBar} and
 * {@link com.google.gwt.user.client.ui.MenuItem}.
 */
public class Deployment extends JBRMSFeature implements Command {

  public static ComponentInfo init() {
    return new ComponentInfo("Deployment",
      "Deploy rulebases to runtime systems.") {
      public JBRMSFeature createInstance() {
        return new Deployment();
      }

	public Image getImage() {	
		return new Image("images/deploy.gif");
	}
    };
  }

  private MenuBar menu = new MenuBar();

  public Deployment() {
    MenuBar subMenu = new MenuBar(true);
    subMenu.addItem("<code>Code</code>", true, this);
    subMenu.addItem("<strike>Strikethrough</strike>", true, this);
    subMenu.addItem("<u>Underlined</u>", true, this);

    MenuBar menu0 = new MenuBar(true);
    menu0.addItem("<b>Bold</b>", true, this);
    menu0.addItem("<i>Italicized</i>", true, this);
    menu0.addItem("More &#187;", true, subMenu);
    MenuBar menu1 = new MenuBar(true);
    menu1.addItem("<font color='#FF0000'><b>Apple</b></font>", true, this);
    menu1.addItem("<font color='#FFFF00'><b>Banana</b></font>", true, this);
    menu1.addItem("<font color='#FFFFFF'><b>Coconut</b></font>", true, this);
    menu1.addItem("<font color='#8B4513'><b>Donut</b></font>", true, this);
    MenuBar menu2 = new MenuBar(true);
    menu2.addItem("Bling", this);
    menu2.addItem("Ginormous", this);
    menu2.addItem("<code>w00t!</code>", true, this);

    menu.addItem(new MenuItem("Style", menu0));
    menu.addItem(new MenuItem("Fruit", menu1));
    menu.addItem(new MenuItem("Term", menu2));

    menu.setWidth("100%");

    setWidget(menu);
  }

  public void execute() {
    Window.alert("Thank you for selecting a menu item.");
  }

  public void onShow() {
  }
}
