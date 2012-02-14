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

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formbuilder.client.bus.UndoRedoEvent;
import org.jbpm.formbuilder.client.bus.UndoRedoHandler;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;
import org.jbpm.formbuilder.client.options.UndoRedoManager;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.MenuItem;
import com.gwtent.reflection.client.Reflectable;

/**
 * In charge of the action of undoing the last action
 */
@Reflectable
public class EditFormUndoCommand implements BaseCommand {

    private final UndoRedoManager mgr = UndoRedoManager.getInstance();
    private MenuItem item = null;
    
    public EditFormUndoCommand() {
        EventBus bus = CommonGlobals.getInstance().getEventBus();
        bus.addHandler(UndoableEvent.TYPE, new UndoableHandler() {
            @Override
            public void onEvent(UndoableEvent event) {
                checkEnabled();
            }
            @Override
            public void undoAction(UndoableEvent event) {   }
            @Override
            public void doAction(UndoableEvent event) {  }
        });
        bus.addHandler(UndoRedoEvent.TYPE, new UndoRedoHandler() {
            @Override
            public void onEvent(UndoRedoEvent event) {
                checkEnabled();
            }
        });
    }
    
    @Override
    public void execute() {
        mgr.undo();
        checkEnabled();
    }

    @Override
    public void setItem(MenuItem item) {
        this.item = item;
        checkEnabled();
    }
    
    @Override
    public void setEmbeded(String profile) {
        //shouldn't be disabled when embedded
    }

    private void checkEnabled() {
        if (this.item != null) {
            this.item.setEnabled(mgr.canUndo());
        }
    }

}
