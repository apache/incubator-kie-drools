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
package org.jbpm.formapi.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.shared.EventBus;

/**
 * 
 */
public class CommonGlobals {

    public static final String FORM_PANEL_KEY = "org.jbpm.formbuilder.FormBuilder.FORM_PANEL";
    public static final String BASE_LOCALE = "org.jbpm.formbuilder.server.render.Renderer.BASE_LOCALE";
    
    private static final CommonGlobals INSTANCE = new CommonGlobals();
    
    private EventBus eventBus;
    private AbstractFormItemCommand copy;
    private AbstractFormItemCommand cut;
    private AbstractFormItemCommand paste;
    private PickupDragController dragController;
    
    private CommonGlobals() {
    }
    
    public static CommonGlobals getInstance() {
        return INSTANCE;
    }
    
    public void registerEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
    
    public EventBus getEventBus() {
        return this.eventBus;
    }
    
    public void registerCopy(AbstractFormItemCommand copy) {
        this.copy = copy;
    }
    
    public void registerCut(AbstractFormItemCommand cut) {
        this.cut = cut;
    }
    
    public void registerPaste(AbstractFormItemCommand paste) {
        this.paste = paste;
    }
    
    public AbstractFormItemCommand copy() {
        return copy;
    }
    
    public AbstractFormItemCommand cut() {
        return cut;
    }
    
    public AbstractFormItemCommand paste() {
        return paste;
    }

    public void registerDragController(PickupDragController dragController) {
        this.dragController = dragController;
    }
    
    public PickupDragController getDragController() {
        return dragController;
    }
}
