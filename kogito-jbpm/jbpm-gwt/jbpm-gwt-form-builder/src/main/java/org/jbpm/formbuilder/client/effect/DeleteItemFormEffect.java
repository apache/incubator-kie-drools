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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
 * Allows to remove an item from a related {@link OptionsFormItem} (for example, a combobox)
 */
@Reflectable
public class DeleteItemFormEffect extends FBFormEffect {

    private String dropItemLabel;
    private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    public DeleteItemFormEffect() {
        super(FormBuilderGlobals.getInstance().getI18n().DeleteItemFormEffectLabel(), true);
    }

    public void setDropItemLabel(String dropItemLabel) {
        this.dropItemLabel = dropItemLabel;
    }
    
    public String getDropItemLabel() {
        return this.dropItemLabel;
    }
    
    @Override
    protected void createStyles() {
        OptionsFormItem opt = (OptionsFormItem) super.getItem();
        opt.deleteItem(getDropItemLabel());
    }
    
    protected void revertStyles(String label, String value) {
        OptionsFormItem opt = (OptionsFormItem) super.getItem();
        opt.addItem(label, value);
    }
    
    protected String getValue(String label) {
        FBFormItem item = super.getItem();
        String value = null;
        if (item instanceof OptionsFormItem) {
            OptionsFormItem opt = (OptionsFormItem) item;
            value = opt.getItems().get(label);
        }
        return value;
    }
    
    @Override
    public PopupPanel createPanel() {
        final PopupPanel panel = new PopupPanel();
        panel.setSize("150px", "66px");
        VerticalPanel vPanel = new VerticalPanel();
        HorizontalPanel hPanel1 = new HorizontalPanel();
        hPanel1.add(new Label(FormBuilderGlobals.getInstance().getI18n().LabelToDelete()));
        final TextBox labelBox = new TextBox();
        labelBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                undoableEffect(panel, labelBox.getValue());
            }
        });
        hPanel1.add(labelBox);
        Button applyButton = new Button(i18n.ConfirmButton());
        applyButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                undoableEffect(panel, labelBox.getValue());
            }
        });        
        vPanel.add(hPanel1);
        vPanel.add(applyButton);
        panel.add(vPanel);
        return panel;
    }

    private void undoableEffect(final PopupPanel panel, final String label) {
        Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        dataSnapshot.put("deletedLabel", label);
        dataSnapshot.put("deletedValue", getValue(label));
        bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override
            public void onEvent(UndoableEvent event) {  }
            @Override
            public void undoAction(UndoableEvent event) {
                String label = (String) event.getData("deletedLabel");
                String value = (String) event.getData("deletedValue");
                revertStyles(label, value);
                panel.hide();
            }
            @Override
            public void doAction(UndoableEvent event) {
                String label = (String) event.getData("deletedLabel");
                DeleteItemFormEffect.this.setDropItemLabel(label);
                createStyles();
                panel.hide();
            }
        }));
    }
    
    @Override
    public boolean isValidForItem(FBFormItem item) {
        return super.isValidForItem(item) && (item instanceof OptionsFormItem);
    }
}
