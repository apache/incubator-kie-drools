/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formapi.common.panels;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * 
 */
public class CommandPopupPanel extends PopupPanel {

    private MenuBar menuBar = new MenuBar(true);
    
    public CommandPopupPanel() {
        super();
        init();
    }

    public CommandPopupPanel(boolean autoHide, boolean modal) {
        super(autoHide, modal);
        init();
    }

    public CommandPopupPanel(boolean autoHide) {
        super(autoHide);
        init();
    }

    private void init() {
        setStyleName("commandPopupPanel");
        menuBar.setStyleName("commandMenuBar");
        setWidget(menuBar);
    }

    public MenuItem addItem(String text, Command cmd) {
        return addItem(new MenuItem(text, cmd));
    }

    public MenuItem addItem(MenuItem item) {
        item.setStyleName("commandMenuItem");
        return menuBar.addItem(item);
    }
}
