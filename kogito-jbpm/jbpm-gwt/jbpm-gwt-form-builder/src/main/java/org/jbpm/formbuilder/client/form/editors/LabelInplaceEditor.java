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
package org.jbpm.formbuilder.client.form.editors;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.form.FBInplaceEditor;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;
import org.jbpm.formbuilder.client.form.items.LabelFormItem;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Inplace editor for {@link LabelFormItem}
 */
public class LabelInplaceEditor extends FBInplaceEditor {

    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    private final TextBox textBox = new TextBox();
    private final FocusWrapper wrapper = new FocusWrapper();
    
    public LabelInplaceEditor(final LabelFormItem item) {
        final HorizontalPanel editPanel = new HorizontalPanel();
        editPanel.setBorderWidth(1);
        textBox.setValue(item.getLabel().getText());
        textBox.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                wrapper.setValue(false);
                item.reset();
            }
        });
        textBox.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                wrapper.setValue(true);
            }
        });
        textBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                Map<String, Object> dataSnapshot = new HashMap<String, Object>();
                dataSnapshot.put("item", item);
                dataSnapshot.put("oldValue", item.getLabel().getText());
                dataSnapshot.put("newValue", textBox.getValue());
                bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
                    @Override
                    public void undoAction(UndoableEvent event) {
                        LabelFormItem myItem = (LabelFormItem) event.getData("item");
                        String value = (String) event.getData("oldValue");
                        myItem.getLabel().setText(value);
                    }
                    @Override
                    public void onEvent(UndoableEvent event) { }
                    @Override
                    public void doAction(UndoableEvent event) {
                        LabelFormItem myItem = (LabelFormItem) event.getData("item");
                        String value = (String) event.getData("newValue");
                        myItem.getLabel().setText(value);
                    }
                }));
                item.reset();
            }
        });
        editPanel.add(textBox);
        textBox.setWidth(item.getLabel().getOffsetWidth() + "px");
        textBox.setFocus(true);
        add(editPanel);
    }
    
    @Override
    public void focus() {
        textBox.setFocus(true);
    }
    
    @Override
    public boolean isFocused() {
        return wrapper.getValue();
    }

}
