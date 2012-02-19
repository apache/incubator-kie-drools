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
package org.jbpm.formbuilder.client.validation;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.formapi.client.validation.FBValidationItem;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SubValidationsList extends VerticalPanel {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final List<FBValidationItem> items = new ArrayList<FBValidationItem>();
    private final String concatText;
    private final HorizontalPanel buttonPanel = new HorizontalPanel();
    private final List<FBValidationItem> existingValidations;
    
    private FBValidationItem selectedItemToAdd = null;
    
    public SubValidationsList(String concatenator, List<FBValidationItem> validations) {
        this.concatText = concatenator;
        this.existingValidations = validations;
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        final ListBox list = new ListBox();
        list.addItem("...", "");
        for (FBValidationItem item : existingValidations) {
            list.addItem(item.getName(), item.createValidation().getValidationId());
        }
        list.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                int index = list.getSelectedIndex() - 1;
                if (index < 0) {
                    selectedItemToAdd = null;
                } else {
                    selectedItemToAdd = existingValidations.get(index).cloneItem();
                }
            }
        });
        buttonPanel.add(list);
        buttonPanel.add(new Button(i18n.AddButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (selectedItemToAdd == null) {
                    Window.alert(i18n.SelectValidationFirstWarning());
                } else {
                    addItem(selectedItemToAdd);
                    list.setSelectedIndex(0);
                    selectedItemToAdd = null;
                }
            }
        }));
        add(buttonPanel);
    }
    
    public List<FBValidationItem> getItems() {
        return items;
    }

    public void addItem(final FBValidationItem item) {
        this.items.add(item);
        final HorizontalPanel itemPanel = new HorizontalPanel();
        itemPanel.add(item.createDisplay());
        itemPanel.add(new Button(i18n.RemoveButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                items.remove(item);
                remove(itemPanel);
            }
        }));
        itemPanel.add(new Label(concatText));
        insert(itemPanel, getWidgetIndex(buttonPanel));
    }

    public void clearItems() {
        while (getWidget(0) != buttonPanel) {
            remove(0);
        }
    }
}
