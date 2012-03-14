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
package org.jbpm.formbuilder.client.layout;

import org.jbpm.formapi.client.form.LayoutFormItem;
import org.jbpm.formbuilder.client.command.DropFormItemController;
import org.jbpm.formbuilder.client.form.FBForm;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * layout view. Represents a single form
 */
public class LayoutViewImpl extends ScrollPanel implements LayoutView {

    private FBForm formDisplay = new FBForm();
    
    public LayoutViewImpl() {
        setStyleName("formDisplay");
        formDisplay.setStyleName("formDisplay");
        formDisplay.setSize("100%", "100%");
        add(formDisplay);
        
        new LayoutPresenter(this);
    }

    @Override
    public void startDropController(PickupDragController controller, IsWidget layout) {
        controller.registerDropController(new DropFormItemController(layout.asWidget(), this));
    }
    
    @Override
    public HasWidgets getUnderlyingLayout(Integer x, Integer y) {
        for (Widget widget : formDisplay) {
            if (widget instanceof LayoutFormItem) {
                LayoutFormItem item = (LayoutFormItem) widget;
                HasWidgets newLayout = item.getUnderlyingLayout(x, y);
                if (newLayout != null) {
                    return newLayout;
                }
            }
        }
        return formDisplay;
    }
    
    @Override
    public FBForm getFormDisplay() {
        return formDisplay;
    }
}
