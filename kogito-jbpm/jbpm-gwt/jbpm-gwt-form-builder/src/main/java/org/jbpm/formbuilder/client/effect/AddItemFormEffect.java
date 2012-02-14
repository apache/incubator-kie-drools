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
package org.jbpm.formbuilder.client.effect;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.form.OptionsFormItem;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtent.reflection.client.Reflectable;

/**
 * Allows to add an item to a related {@link OptionsFormItem} (for example, a combobox)
 */
@Reflectable
public class AddItemFormEffect extends FBFormEffect {

    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    
    private String newLabel;
    private String newValue;
    
    public AddItemFormEffect() {
        super(FormBuilderGlobals.getInstance().getI18n().AddItemFormEffectLabel(), true);
    }
    
    public String getNewLabel() {
        return newLabel;
    }

    public void setNewLabel(String newLabel) {
        this.newLabel = newLabel;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    @Override
    protected void createStyles() {
        OptionsFormItem opt = (OptionsFormItem) super.getItem();
        opt.addItem(getNewLabel(), getNewValue());
    }
    
    protected void revertStyles(String label, FBFormItem item) {
        OptionsFormItem opt = (OptionsFormItem) item;
        opt.deleteItem(label);
    }

    @Override
    public PopupPanel createPanel() {
        final PopupPanel panel = new PopupPanel();
        panel.setSize("150px", "100px");
        VerticalPanel vPanel = new VerticalPanel();
        HorizontalPanel hPanel1 = new HorizontalPanel();
        hPanel1.add(new Label(i18n.NewItemLabel()));
        final TextBox labelBox = new TextBox();
        hPanel1.add(labelBox);
        HorizontalPanel hPanel2 = new HorizontalPanel();
        hPanel2.add(new Label(i18n.NewItemValue()));
        final TextBox valueBox = new TextBox();
        hPanel2.add(valueBox);
        Button applyButton = new Button(i18n.ConfirmButton());
        applyButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Map<String, Object> dataSnapshot = new HashMap<String, Object>();
                dataSnapshot.put("labelBoxValue", labelBox.getValue());
                dataSnapshot.put("valueBoxValue", valueBox.getValue());
                dataSnapshot.put("item", getItem());
                EventBus bus = CommonGlobals.getInstance().getEventBus();
                bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
                    @Override
                    public void onEvent(UndoableEvent event) {  }
                    @Override
                    public void undoAction(UndoableEvent event) {
                        String label = (String) event.getData("labelBoxValue");
                        FBFormItem item = (FBFormItem) event.getData("item");
                        revertStyles(label, item);
                    }
                    @Override
                    public void doAction(UndoableEvent event) {
                        String newLabel = (String) event.getData("labelBoxValue");
                        String newValue = (String) event.getData("valueBoxValue");
                        setNewLabel(newLabel);
                        setNewValue(newValue);
                        createStyles();
                    }
                }));
                panel.hide();
            }
        });        
        vPanel.add(hPanel1);
        vPanel.add(hPanel2);
        vPanel.add(applyButton);
        panel.add(vPanel);
        return panel;
    }

    @Override
    public boolean isValidForItem(FBFormItem item) {
        return super.isValidForItem(item) && (item instanceof OptionsFormItem);
    }
}
