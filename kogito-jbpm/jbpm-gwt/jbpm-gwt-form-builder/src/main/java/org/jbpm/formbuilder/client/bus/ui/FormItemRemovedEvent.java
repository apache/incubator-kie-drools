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

import org.jbpm.formapi.client.form.FBFormItem;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Notifies a form item has been removed from the layout
 */
public class FormItemRemovedEvent extends GwtEvent<FormItemRemovedHandler> {

    public static final Type<FormItemRemovedHandler> TYPE = new Type<FormItemRemovedHandler>();
    
    private final FBFormItem formItem;
    
    public FormItemRemovedEvent(FBFormItem formItem) {
        super();
        this.formItem = formItem;
    }

    public FBFormItem getFormItem() {
        return formItem;
    }
    
    @Override
    public Type<FormItemRemovedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FormItemRemovedHandler handler) {
        handler.onEvent(this);
    }

}
