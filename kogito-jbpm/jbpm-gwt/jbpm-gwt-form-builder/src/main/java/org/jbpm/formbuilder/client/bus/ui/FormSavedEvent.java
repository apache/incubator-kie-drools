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

import org.jbpm.formapi.shared.api.FormRepresentation;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Notifies a form has already been saved on the server
 */
public class FormSavedEvent extends GwtEvent<FormSavedHandler> {

    public static final Type<FormSavedHandler> TYPE = new Type<FormSavedHandler>();
    
    private final FormRepresentation form;
    
    public FormSavedEvent(FormRepresentation form) {
        this.form = form;
    }
    
    public FormRepresentation getForm() {
        return form;
    }
    
    @Override
    public Type<FormSavedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FormSavedHandler handler) {
        handler.onEvent(this);
    }

}
