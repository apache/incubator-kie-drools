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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Simple confirm dialog box to show warnings
 */
public class ConfirmDialog extends DialogBox {

    private final Label warning = new Label(""); 
    private final Button okButton = new Button("Continue");
    
    public ConfirmDialog(String warningText) {
        super(false, true);
        warning.setText(warningText);
        VerticalPanel panel = new VerticalPanel();
        panel.add(warning);
        HorizontalPanel buttons = new HorizontalPanel();
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        buttons.add(okButton);
        buttons.add(cancelButton);
        panel.add(buttons);
        add(panel);
        int height = RootPanel.getBodyElement().getClientHeight();
        int width = RootPanel.getBodyElement().getClientWidth();
        setSize("300px", "100px");
        setPopupPosition(width / 2 - 150, height / 2 - 50);
    }
    
    public void addOkButtonHandler(ClickHandler handler) {
        okButton.addClickHandler(handler);
    }
}
