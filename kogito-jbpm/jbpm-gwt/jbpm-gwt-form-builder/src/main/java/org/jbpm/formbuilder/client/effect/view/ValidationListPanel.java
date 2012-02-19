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
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.validation.FBValidationItem;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class ValidationListPanel extends HorizontalPanel {

    private static final String NULL_VALIDATION = "...";
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final Button addValidationButton = new Button(i18n.AddValidationButton());
    private final ListBox validationsAvailableList = new ListBox();
    
    private Map<String, FBValidationItem> availableValidations = new HashMap<String, FBValidationItem>();
    
    public ValidationListPanel() {
        add(new Label(i18n.ValidationTypeLabel()));
        add(validationsAvailableList);
        add(addValidationButton);
    }
    
    public void onAdd(ClickHandler handler) {
        addValidationButton.addClickHandler(handler);
    }
    
    public FBValidationItem getValidationSelection() {
        String validationKey = validationsAvailableList.getValue(validationsAvailableList.getSelectedIndex());
        return (validationKey.equals(NULL_VALIDATION) ? null : availableValidations.get(validationKey).cloneItem());
    }
    
    public void setAvailableValidations(List<FBValidationItem> availableValidations) {
        this.validationsAvailableList.clear();
        this.availableValidations.clear();
        this.validationsAvailableList.addItem(NULL_VALIDATION);
        for (FBValidationItem validation : availableValidations) {
            this.availableValidations.put(validation.getName(), validation);
            this.validationsAvailableList.addItem(validation.getName(), validation.getName());
        }
    }

}
