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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jbpm.formapi.client.validation.FBValidationItem;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ValidationEditionPanel extends VerticalPanel {
    
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final Grid editionGrid = new Grid(1, 1);
    private final Map<String, HasValue<String>> validationProperties = new HashMap<String, HasValue<String>>();
    private final Button okButton = new Button(i18n.OkButton());
    
    private FBValidationItem currentValidation = null;
    
    public ValidationEditionPanel() {
        add(editionGrid);
        HorizontalPanel hPanel = new HorizontalPanel();
        Button resetButton = new Button(i18n.ResetButton());
        resetButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setVisible(false);
            }
        });
        hPanel.add(resetButton);
        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setVisible(false);
            }
        });
        hPanel.add(okButton);
        add(hPanel);
    }
    
    public void onCommitEdition(ClickHandler handler) {
        okButton.addClickHandler(handler);
    }
    
    public void setCurrentValidation(FBValidationItem newValidation) {
        this.currentValidation = newValidation;
        Widget display = newValidation.createDisplay();
        if (display == null) {
            Map<String, HasValue<String>> newValidationProperties = newValidation.getPropertiesMap();
            int propertiesSize = (newValidationProperties == null ? 0 : newValidationProperties.size());
            editionGrid.clear();
            editionGrid.resize(propertiesSize, 3);
            validationProperties.clear();
            if (newValidationProperties != null) {
                Iterator<Map.Entry<String, HasValue<String>>> iter = newValidationProperties.entrySet().iterator(); 
                for (int index = 0; iter.hasNext(); index++) {
                    Map.Entry<String, HasValue<String>> entry = iter.next();
                    editionGrid.setWidget(index, 0, new Label(entry.getKey() + ": "));
                    editionGrid.setWidget(index, 1, new HTML("&nbsp;&nbsp;&nbsp;"));
                    validationProperties.put(entry.getKey(), entry.getValue());
                    editionGrid.setWidget(index, 2, (Widget) entry.getValue());
                }
            }
        } else {
            editionGrid.clear();
            editionGrid.resize(1, 1);
            editionGrid.setWidget(0, 0, display );
        }
        setVisible(true);
    }
    
    public FBValidationItem getCurrentValidation() {
        return currentValidation;
    }
}
