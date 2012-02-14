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

import org.jbpm.formapi.client.form.LayoutFormItem;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Notifies a layout item has been added to the form, and needs to be registered as a droppable zone.
 */
public class RegisterLayoutEvent extends GwtEvent<RegisterLayoutHandler> {

    public static final Type<RegisterLayoutHandler> TYPE = new Type<RegisterLayoutHandler>();
    
    private final LayoutFormItem layout;
    
    public RegisterLayoutEvent(LayoutFormItem layout) {
        this.layout = layout;
    }
    
    public LayoutFormItem getLayout() {
        return layout;
    }
    
    @Override
    public Type<RegisterLayoutHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RegisterLayoutHandler handler) {
        handler.onEvent(this);
    }

    
}
