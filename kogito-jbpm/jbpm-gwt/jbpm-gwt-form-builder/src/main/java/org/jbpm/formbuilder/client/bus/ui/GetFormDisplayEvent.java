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
package org.jbpm.formbuilder.client.bus.ui;

import org.jbpm.formbuilder.client.form.FBForm;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Asks for the current FBForm instance to the default clients
 */
public class GetFormDisplayEvent extends GwtEvent<GetFormDisplayHandler> {

    public static final Type<GetFormDisplayHandler> TYPE = new Type<GetFormDisplayHandler>();
    
    private FBForm formDisplay;
    
    public GetFormDisplayEvent() {
        super();
    }
    
    public FBForm getFormDisplay() {
        return formDisplay;
    }

    public void setFormDisplay(FBForm formDisplay) {
        this.formDisplay = formDisplay;
    }

    @Override
    public Type<GetFormDisplayHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(GetFormDisplayHandler handler) {
        handler.onEvent(this);
    }

}
