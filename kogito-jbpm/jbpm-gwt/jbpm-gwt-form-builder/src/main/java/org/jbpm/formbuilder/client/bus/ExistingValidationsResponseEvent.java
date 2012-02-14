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

import org.jbpm.formapi.client.validation.FBValidationItem;

import com.google.gwt.event.shared.GwtEvent;

public class ExistingValidationsResponseEvent extends GwtEvent<ExistingValidationsResponseHandler> {

    public static final Type<ExistingValidationsResponseHandler> TYPE = new Type<ExistingValidationsResponseHandler>();
    
    private final List<FBValidationItem> existingValidations;
    
    public ExistingValidationsResponseEvent(List<FBValidationItem> existingValidations) {
        super();
        this.existingValidations = existingValidations;
    }
    
    public List<FBValidationItem> getExistingValidations() {
        return existingValidations;
    }

    @Override
    public Type<ExistingValidationsResponseHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ExistingValidationsResponseHandler handler) {
        handler.onEvent(this);
    }

}
