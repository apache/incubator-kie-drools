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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.shared.api.FBScript;
import org.jbpm.formbuilder.client.bus.UndoableEvent;
import org.jbpm.formbuilder.client.bus.UndoableHandler;
import org.jbpm.formbuilder.client.effect.view.EventHandlingEffectView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.PopupPanel;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class EventHandlingFormEffect extends FBFormEffect {

    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    public EventHandlingFormEffect() {
        super("Event handling", true);
    }
    
    @Override
    protected void createStyles() {
    }

    @Override
    public PopupPanel createPanel() {
        return new EventHandlingEffectView(this);
    }
    
    @Override
    public boolean isValidForItem(FBFormItem item) {
        return super.isValidForItem(item) && item.getEventActions() != null && !item.getEventActions().isEmpty();
    }
    
    public Map<String, FBScript> getItemActions() {
        return getItem().getEventActions();
    }
    
    public List<String> getPossibleEvents() {
        Map<String, FBScript> eventActions = getItem().getEventActions();
        if (eventActions != null) {
            return new ArrayList<String>(eventActions.keySet());
        }
        return new ArrayList<String>();
    }
    
    public void storeEventAction(String eventName, FBScript script) {
        Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        dataSnapshot.put("oldScript", getItem().getEventActions().get(eventName));
        dataSnapshot.put("newScript", script);
        dataSnapshot.put("eventName", eventName);
        dataSnapshot.put("eventActions", getItem().getEventActions());
        dataSnapshot.put("item", getItem());
        bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override
            @SuppressWarnings("unchecked")
            public void undoAction(UndoableEvent event) {
                FBScript script = (FBScript) event.getData("oldScript");
                String eventName = (String) event.getData("eventName");
                Map<String, FBScript> eventActions = (Map<String, FBScript>) event.getData("eventActions");
                FBFormItem item = (FBFormItem) event.getData("item");
                eventActions.put(eventName, script);
                item.setEventActions(eventActions);
            }
            @Override
            public void onEvent(UndoableEvent event) { }
            @Override
            @SuppressWarnings("unchecked")
            public void doAction(UndoableEvent event) {
                FBScript script = (FBScript) event.getData("newScript");
                String eventName = (String) event.getData("eventName");
                Map<String, FBScript> eventActions = (Map<String, FBScript>) event.getData("eventActions");
                FBFormItem item = (FBFormItem) event.getData("item");
                eventActions.put(eventName, script);
                item.setEventActions(eventActions);
            }
        }));
    }
    
    public void confirmEventAction(String eventName, FBScript script) {
        Map<String, Object> dataSnapshot = new HashMap<String, Object>();
        dataSnapshot.put("oldScript", getItem().getEventActions().get(eventName));
        dataSnapshot.put("newScript", script);
        dataSnapshot.put("eventActions", getItemActions());
        dataSnapshot.put("eventName", eventName);
        dataSnapshot.put("item", getItem());
        bus.fireEvent(new UndoableEvent(dataSnapshot, new UndoableHandler() {
            @Override @SuppressWarnings("unchecked")
            public void undoAction(UndoableEvent event) {
                FBScript script = (FBScript) event.getData("oldScript");
                String eventName = (String) event.getData("eventName");
                FBFormItem item = (FBFormItem) event.getData("item");
                Map<String, FBScript> eventActions = (Map<String, FBScript>) event.getData("eventActions");
                eventActions.put(eventName, script);
                item.setEventActions(eventActions);
            }
            @Override
            public void onEvent(UndoableEvent event) { }
            @Override @SuppressWarnings("unchecked")
            public void doAction(UndoableEvent event) {
                FBScript script = (FBScript) event.getData("newScript");
                String eventName = (String) event.getData("eventName");
                FBFormItem item = (FBFormItem) event.getData("item");
                Map<String, FBScript> eventActions = (Map<String, FBScript>) event.getData("eventActions");
                eventActions.put(eventName, script);
                item.setEventActions(eventActions);
            }
        }));
    }
}
