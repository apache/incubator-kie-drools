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
package org.jbpm.formbuilder.client.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.shared.api.InputData;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InputMapPanel extends PopupPanel {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final Button okButton = new Button(i18n.OkButton());
    private final Button cancelButton = new Button(i18n.CancelButton());
    private final Map<String, InputData> inputs;
    
    private final Map<String, Object> retData = new HashMap<String, Object>();
    
    public InputMapPanel(Map<String, InputData> inputs) {
        super(false, true);
        this.inputs = inputs;
        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        mainPanel.add(new Label(i18n.InputMapPopulation()));
        mainPanel.add(createInputTable());
        mainPanel.add(createButtonPanel());
        setWidget(mainPanel);
    }

    private Grid createInputTable() {
        Grid inputTable = new Grid(this.inputs.size(), 2);
        List<InputData> inputList = new ArrayList<InputData>(this.inputs.values());
        for (int index = 0; index < inputList.size(); index++) {
            final InputData input = inputList.get(index);
            populateRetData(input);
            inputTable.setWidget(index, 0, new Label(input.getName()));
            final TextBox inputText = new TextBox();
            inputText.setValue(input.getValue());
            inputText.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    input.setValue(inputText.getValue());
                    populateRetData(input);
                }
            });
            inputTable.setWidget(index, 1, inputText);
        }
        return inputTable;
    }

    private void populateRetData(final InputData input) {
        if (input.getFormatter() == null) {
            retData.put(input.getName(), input.getValue());
        } else {
            retData.put(input.getName(), input.getFormatter().format(input.getValue()));
        }
    }

    private HorizontalPanel createButtonPanel() {
        HorizontalPanel buttons = new HorizontalPanel();
        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        buttons.add(okButton);
        buttons.add(cancelButton);
        return buttons;
    }
    
    public HandlerRegistration addOkHandler(ClickHandler handler) {
        return okButton.addClickHandler(handler);
    }
    
    public Map<String, Object> getInputs() {
        return retData; 
    }
}
