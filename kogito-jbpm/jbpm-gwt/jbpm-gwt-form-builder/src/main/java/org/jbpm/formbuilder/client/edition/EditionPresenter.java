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
package org.jbpm.formbuilder.client.edition;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.bus.FormItemSelectionEvent;
import org.jbpm.formapi.client.bus.FormItemSelectionHandler;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;

import com.google.gwt.event.shared.EventBus;

/**
 * Populates edition panel when a form item is selected
 */
public class EditionPresenter implements EditionView.Presenter {

    private final EditionView editView;
    private final EventBus bus;
    
    public EditionPresenter(EditionView view) {
        super();
        this.editView = view;
        this.bus = CommonGlobals.getInstance().getEventBus();
        
        this.bus.addHandler(FormItemSelectionEvent.TYPE, new FormItemSelectionHandler() {
            @Override
            public void onEvent(FormItemSelectionEvent event) {
                if (event.isSelected()) {
                    editView.selectTab();
                    editView.populate(event.getFormItemSelected());
                }
            }
        });
    }

    @Override
    public void onSaveChanges(Map<String, Object> oldProps, Map<String, Object> newProps, FBFormItem itemSelected) {
        Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        dataSnapshot.put("oldItems", oldProps);
        dataSnapshot.put("newItems", newProps);
        dataSnapshot.put("itemSelected", itemSelected);
        bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override
            public void onEvent(UndoableEvent event) {  }
            @Override
            @SuppressWarnings("unchecked")
            public void undoAction(UndoableEvent event) {
                FBFormItem itemSelected = (FBFormItem) event.getData("itemSelected");
                itemSelected.saveValues((Map<String, Object>) event.getData("oldItems"));
            }
            @Override
            @SuppressWarnings("unchecked")
            public void doAction(UndoableEvent event) {
                FBFormItem itemSelected = (FBFormItem) event.getData("itemSelected");
                itemSelected.saveValues((Map<String, Object>) event.getData("newItems"));
            }
        }));
    }
    
    @Override
    public void onResetChanges(FBFormItem fakeItem, Map<String, Object> newItems) {
        Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        dataSnapshot.put("newItems", newItems);
        dataSnapshot.put("fakeItemSelected", fakeItem);
        bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override
            public void onEvent(UndoableEvent event) {  }
            @Override
            @SuppressWarnings("unchecked")
            public void undoAction(UndoableEvent event) {
                FBFormItem itemSelected = (FBFormItem) event.getData("fakeItemSelected");
                itemSelected.saveValues((Map<String, Object>) event.getData("newItems"));
                editView.populate(itemSelected);
            }
            @Override
            public void doAction(UndoableEvent event) {
                FBFormItem itemSelected = (FBFormItem) event.getData("fakeItemSelected");
                editView.populate(itemSelected);
            }
        }));
    }
}
