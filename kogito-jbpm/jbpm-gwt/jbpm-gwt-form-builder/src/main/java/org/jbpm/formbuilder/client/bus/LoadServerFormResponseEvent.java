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

import java.util.List;

import org.jbpm.formapi.shared.api.FormRepresentation;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Notifies that the server has found a (list of) form(s) as requested
 */
public class LoadServerFormResponseEvent extends GwtEvent<LoadServerFormResponseHandler> {

    public static final Type<LoadServerFormResponseHandler> TYPE = new Type<LoadServerFormResponseHandler>();
    
    private final List<FormRepresentation> list;
    private final FormRepresentation form;
    
    public LoadServerFormResponseEvent(FormRepresentation formRepresentation) {
        this.list = null;
        this.form = formRepresentation;
    }
    
    public LoadServerFormResponseEvent(List<FormRepresentation> loadedForms) {
        this.list = loadedForms;
        this.form = null;
    }

    public FormRepresentation getForm() {
        return form;
    }
    
    public List<FormRepresentation> getList() {
        return list;
    }
    
    @Override
    public Type<LoadServerFormResponseHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(LoadServerFormResponseHandler handler) {
        if (this.form != null) {
            handler.onGetForm(this);
        } else if (this.list != null) {
            handler.onListForms(this);
        }
    }

}
