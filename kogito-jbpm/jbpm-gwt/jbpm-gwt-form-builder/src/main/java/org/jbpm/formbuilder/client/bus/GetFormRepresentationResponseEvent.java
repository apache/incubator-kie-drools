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

import org.jbpm.formapi.shared.api.FormRepresentation;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Tells whoever asked that the form representation of the current client edition has been loaded
 */
public class GetFormRepresentationResponseEvent extends
        GwtEvent<GetFormRepresentationResponseHandler> {

    public static final Type<GetFormRepresentationResponseHandler> TYPE = new Type<GetFormRepresentationResponseHandler>();
    
    private final FormRepresentation representation;
    private final String saveType;
    
    public GetFormRepresentationResponseEvent(FormRepresentation representation, String saveType) {
        super();
        this.representation = representation;
        this.saveType = saveType;
    }

    public FormRepresentation getRepresentation() {
        return representation;
    }
    
    public String getSaveType() {
        return saveType;
    }

    @Override
    public Type<GetFormRepresentationResponseHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(GetFormRepresentationResponseHandler handler) {
        handler.onEvent(this);
    }

}
