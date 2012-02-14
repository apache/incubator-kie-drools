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
package org.jbpm.formbuilder.client.bus;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;

/**
 * event for undo/redo
 */
public class UndoableEvent extends GwtEvent<UndoableHandler> {

    public static final Type<UndoableHandler> TYPE = new Type<UndoableHandler>();
    
    private final UndoableHandler rollbackHandler;
    private final Map<String, Object> dataSnapshot;

    public UndoableEvent(Map<String, Object> dataSnapshot, UndoableHandler rollbackHandler) {
        this.rollbackHandler = rollbackHandler;
        if (dataSnapshot == null) {
            dataSnapshot = new HashMap<String, Object>();
        }
        this.dataSnapshot = dataSnapshot;
        this.rollbackHandler.doAction(this);
    }
    
    public UndoableHandler getRollbackHandler() {
        return rollbackHandler;
    }
    
    public Map<String, Object> getDataSnapshot() {
        return dataSnapshot;
    }
    
    public Object getData(String key) {
        return dataSnapshot.get(key);
    }
    
    public void setData(String key, Object value) {
        dataSnapshot.put(key, value);
    }
    
    @Override
    public Type<UndoableHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UndoableHandler handler) {
        handler.onEvent(this);
    }

}
