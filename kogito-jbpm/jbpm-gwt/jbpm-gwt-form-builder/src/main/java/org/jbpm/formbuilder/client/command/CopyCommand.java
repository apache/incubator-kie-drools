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

import com.google.gwt.user.client.ui.MenuItem;
import com.gwtent.reflection.client.Reflectable;

/**
 * Handles the action of copying a ui component
 */
@Reflectable
public class CopyCommand extends AbstractCopyPasteCommand {

    public CopyCommand() {
        super();
        CommonGlobals.getInstance().registerCopy(this);
    }
    
    @Override
    protected void enable(MenuItem menuItem) {
        menuItem.setEnabled(getSelectedItem() != null);
    }
    
    @Override
    public void execute() {
        Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        dataSnapshot.put("selectedItem", getSelectedItem());
        dataSnapshot.put("oldMemory", AbstractCopyPasteCommand.getMemory());
        fireUndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override
            public void doAction(UndoableEvent event) {
                FBFormItem item = (FBFormItem) event.getData("selectedItem");
                if (item == null) {
                    AbstractCopyPasteCommand.setMemory(null);
                } else {
                    AbstractCopyPasteCommand.setMemory(item.cloneItem());
                }
                CommonGlobals.getInstance().paste().enable();
            }
            @Override
            public void undoAction(UndoableEvent event) {
                Object oldMemory = event.getData("oldMemory");
                AbstractCopyPasteCommand.setMemory(oldMemory);
                CommonGlobals.getInstance().paste().enable();
            }
            @Override
            public void onEvent(UndoableEvent event) { }
        });
    }
}
