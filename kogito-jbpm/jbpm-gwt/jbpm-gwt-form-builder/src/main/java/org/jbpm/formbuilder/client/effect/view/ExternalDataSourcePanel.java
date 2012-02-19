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
package org.jbpm.formbuilder.client.effect.view;

import org.jbpm.formbuilder.shared.task.ExternalDataRef;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 */
public class ExternalDataSourcePanel extends Grid {

    private boolean isValid = false;
    private final TextBox sourceTextBox = new TextBox();
    private final ListBox methodListBox = new ListBox();
    private final ListBox responseListBox = new ListBox();
    private final TextBox xpathTextBox = new TextBox();
    
    public ExternalDataSourcePanel() {
        super(4, 2);
        
        methodListBox.addItem("GET");
        methodListBox.addItem("POST");
        methodListBox.setSelectedIndex(0);
        responseListBox.addItem("xml");
        responseListBox.addItem("json");
        responseListBox.setSelectedIndex(0);
        
        setWidget(0, 0, new Label("Source:"));
        setWidget(0, 1, sourceTextBox);
        setWidget(1, 0, new Label("Method:"));
        setWidget(1, 1, methodListBox);
        setWidget(2, 0, new Label("Response lang:"));
        setWidget(2, 1, responseListBox);
        setWidget(3, 0, new Label("Response xpath:"));
        setWidget(3, 1, xpathTextBox);
    }
    
    public ExternalDataRef getData() {
        if (isValid) {
            ExternalDataRef ref = new ExternalDataRef();
            ref.setSource(sourceTextBox.getValue());
            ref.setMethod(methodListBox.getValue(methodListBox.getSelectedIndex()));
            ref.setResponseLanguage(responseListBox.getValue(responseListBox.getSelectedIndex()));
            ref.setXpath(xpathTextBox.getValue());
            return ref;
        }
        return null;
    }

    public void flagData() {
        this.isValid = true;
    }

    public void unflagData() {
        this.isValid = false;
    }
    
    
}
