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

import com.google.gwt.event.shared.GwtEvent;

/**
 * Notifies to the server facade that we need to go look for a form on the server
 */
public class LoadServerFormEvent extends GwtEvent<LoadServerFormHandler> {

    public static final Type<LoadServerFormHandler> TYPE = new Type<LoadServerFormHandler>();
    
    private final String formName;
    
    public LoadServerFormEvent() {
        this(null);
    }
    
    public LoadServerFormEvent(String formName) {
        super();
        this.formName = formName;
    }
    
    public String getFormName() {
        return formName;
    }

    @Override
    public Type<LoadServerFormHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(LoadServerFormHandler handler) {
        handler.onEvent(this);
    }

}
