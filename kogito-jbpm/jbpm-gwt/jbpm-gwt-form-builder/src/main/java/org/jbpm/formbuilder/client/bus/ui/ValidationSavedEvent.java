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

import java.util.List;

import org.jbpm.formapi.client.validation.FBValidationItem;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Notifies a validation has been saved on the server
 */
public class ValidationSavedEvent extends GwtEvent<ValidationSavedHandler> {

    public static final Type<ValidationSavedHandler> TYPE = new Type<ValidationSavedHandler>();
    
    private final List<FBValidationItem> validations;
    
    public ValidationSavedEvent(List<FBValidationItem> validations) {
        this.validations = validations;
    }
    
    public List<FBValidationItem> getValidations() {
        return validations;
    }

    @Override
    public Type<ValidationSavedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ValidationSavedHandler handler) {
        handler.onEvent(this);
    }

}
