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

import java.util.Map;

import org.jbpm.formapi.client.AbstractFormItemCommand;
import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.bus.FormItemSelectionEvent;
import org.jbpm.formapi.client.bus.FormItemSelectionHandler;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * cut / copy / paste command base class
 */
public abstract class AbstractCopyPasteCommand extends AbstractFormItemCommand implements BaseCommand {

    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    private static Object memory;
    
    public AbstractCopyPasteCommand() {   
        bus.addHandler(FormItemSelectionEvent.TYPE, new FormItemSelectionHandler() {
            @Override
            public void onEvent(FormItemSelectionEvent event) {
                if (event.isSelected()) {
                    setSelectedItem(event.getFormItemSelected());
                } else {
                    setSelectedItem(null);
                }
            }
        });
    }
    
    protected void fireUndoableEvent(Map<String, Object> dataSnapshot, UndoableHandler handler) {
        bus.fireEvent(new UndoableEvent(dataSnapshot, handler));
    }
    
    @Override
    public void setItem(MenuItem item) {
        super.setItem(item);
        setSelectedItem(getSelectedItem());
    }
    
    @Override
    public void setEmbeded(String profile) {
        //shouldn't be disabled on embeded
    }
    
    protected static Object getMemory() {
        return memory;
    }
    
    protected static void setMemory(Object object) {
        memory = object;
    }
}
