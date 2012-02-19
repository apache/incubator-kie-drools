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

import java.util.List;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.validation.FBValidationItem;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.bus.ui.ItemValidationsEditedEvent;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ValidationsEffectView extends VerticalPanel {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();

    private PopupPanel parentPopup = null;
    
    private final ValidationListPanel validationListPanel = new ValidationListPanel();
    private final ValidationEditionPanel validationEditionPanel = new ValidationEditionPanel();
    private final ValidationTablePanel validationTablePanel = new ValidationTablePanel();
    
    public ValidationsEffectView() {
        validationListPanel.onAdd(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FBValidationItem validation = validationListPanel.getValidationSelection();
                if (validation != null) {
                    validationEditionPanel.setCurrentValidation(validation);
                    validationEditionPanel.setVisible(true);
                }
            }
        });
        validationEditionPanel.onCommitEdition(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FBValidationItem validation = validationEditionPanel.getCurrentValidation();
                validationTablePanel.addValidation(validation);
            }
        });
        validationTablePanel.addSelectionHandler(new SelectionHandler<FBValidationItem>() {
            @Override
            public void onSelection(SelectionEvent<FBValidationItem> event) {
                FBValidationItem item = event.getSelectedItem();
                validationEditionPanel.setCurrentValidation(item);
            }
        });
        add(validationListPanel);
        add(validationEditionPanel);
        add(validationTablePanel);
        add(createButtonsPanel());
    }
    
    public void setAvailableValidations(List<FBValidationItem> availableValidations) {
        validationListPanel.setAvailableValidations(availableValidations);
    }

    private Panel createButtonsPanel() {
        HorizontalPanel panel = new HorizontalPanel();
        Button applyButton = new Button(i18n.ConfirmButton());
        applyButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                bus.fireEventFromSource(new ItemValidationsEditedEvent(validationTablePanel.getCurrentValidations()), ValidationsEffectView.this);
                if (parentPopup != null) {
                    parentPopup.hide();
                }
            }
        });
        Button cancelButton = new Button(i18n.CancelButton());
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (parentPopup != null) {
                    parentPopup.hide();
                }
            }
        });
        panel.add(applyButton);
        panel.add(cancelButton);
        return panel;
    }
    
    public void setParentPopup(PopupPanel popup) {
        this.parentPopup = popup;
    }
}
