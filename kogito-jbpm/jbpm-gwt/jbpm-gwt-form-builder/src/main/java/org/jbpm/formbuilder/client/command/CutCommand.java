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

import java.util.HashMap;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;
import org.jbpm.formbuilder.client.bus.ui.FormItemAddedEvent;
import org.jbpm.formbuilder.client.bus.ui.FormItemRemovedEvent;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

/**
 * Handles the action of cutting a UI component
 */
@Reflectable
public class CutCommand extends AbstractCopyPasteCommand {

    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    public CutCommand() {
        super();
        CommonGlobals.getInstance().registerCut(this);
    }
    
    @Override
    protected void enable(MenuItem menuItem) {
        menuItem.setEnabled(getSelectedItem() != null);
    }
    
    @Override
    public void execute() {
        Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        dataSnapshot.put("selectedItem", getSelectedItem());
        dataSnapshot.put("oldItemParent", getSelectedItem() == null ? null : getSelectedItem().getParent());
        dataSnapshot.put("oldMemory", AbstractCopyPasteCommand.getMemory());
        fireUndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override
            public void doAction(UndoableEvent event) {
                FBFormItem item = (FBFormItem) event.getData("selectedItem");
                if (item == null) {
                    AbstractCopyPasteCommand.setMemory(null);
                } else {
                    AbstractCopyPasteCommand.setMemory(item.cloneItem());
                    item.removeFromParent();
                }
                CommonGlobals.getInstance().paste().enable();
                bus.fireEvent(new FormItemRemovedEvent(item));
            }
            @Override
            public void undoAction(UndoableEvent event) {
                FBFormItem item = (FBFormItem) event.getData("selectedItem");
                Object oldMemory = event.getData("oldMemory");
                Widget oldParent = (Widget) event.getData("oldItemParent");
                AbstractCopyPasteCommand.setMemory(oldMemory);
                CommonGlobals.getInstance().paste().enable();
                if (oldParent instanceof HasWidgets) {
                    HasWidgets oldParentPanel = (HasWidgets) oldParent;
                    oldParentPanel.add(item);
                } else if (oldParent instanceof HasOneWidget) {
                    HasOneWidget oldParentPanel = (HasOneWidget) oldParent;
                    oldParentPanel.setWidget(item);
                }
                bus.fireEvent(new FormItemAddedEvent(item, oldParent));
            }
            @Override
            public void onEvent(UndoableEvent event) { }
        });
    }
}
