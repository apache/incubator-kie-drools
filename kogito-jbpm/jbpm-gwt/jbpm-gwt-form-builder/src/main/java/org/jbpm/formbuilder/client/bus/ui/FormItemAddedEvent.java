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

import org.jbpm.formapi.client.form.FBCompositeItem;
import org.jbpm.formapi.client.form.FBFormItem;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * Notifies that a new instance of FBFormItem has been added to the layout
 */
public class FormItemAddedEvent extends GwtEvent<FormItemAddedHandler> {

    public static final Type<FormItemAddedHandler> TYPE = new Type<FormItemAddedHandler>();
    
    private final FBFormItem formItem;
    private final Widget formItemHolder;
    
    public FormItemAddedEvent(FBFormItem formItem, Widget formItemHolder) {
        super();
        this.formItem = formItem;
        this.formItemHolder = formItemHolder;
    }

    public FBFormItem getFormItem() {
        return formItem;
    }
    
    public Widget getFormItemHolder() {
        return formItemHolder;
    }
    
    @Override
    public Type<FormItemAddedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FormItemAddedHandler handler) {
        handler.onEvent(this);
        if (formItem instanceof FBCompositeItem) {
            FBCompositeItem comboItem = (FBCompositeItem) formItem;
            if (comboItem.getItems() != null) {
                for (FBFormItem item : comboItem.getItems()) {
                    FormItemAddedEvent event = new FormItemAddedEvent(item, formItem);
                    event.dispatch(handler);
                }
            }
        }
    }

}
