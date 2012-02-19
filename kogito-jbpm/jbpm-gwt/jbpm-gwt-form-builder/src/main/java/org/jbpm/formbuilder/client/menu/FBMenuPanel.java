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
package org.jbpm.formbuilder.client.menu;

import org.jbpm.formapi.client.menu.FBMenuItem;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Accordion piece panel. Handles a list of menu items for
 * a given group name
 */
public class FBMenuPanel extends VerticalPanel {

    private DragController dragController;

    public FBMenuPanel(DragController dragController) {
        this.dragController = dragController;
        setSpacing(2);
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
    }

    /**
     * Overloaded method that makes widgets draggable.
     * 
     * @param w
     *            the widget to be added are made draggable
     */
    public void add(FBMenuItem menuItem) {
        this.dragController.makeDraggable(menuItem);
        super.add(menuItem);
    }

    /**
     * Removed widgets that are instances of {@link FBMenuItem} are
     * immediately replaced with a cloned copy of the original.
     * 
     * @param w
     *            the widget to remove
     * @return true if a widget was removed
     */
    @Override
    public boolean remove(Widget w) {
        int index = getWidgetIndex(w);
        if (index != -1 && w instanceof FBMenuItem) {
            FBMenuItem item = ((FBMenuItem) w).cloneWidget();
            dragController.makeDraggable(item);
            insert(item, index);
        }
        return super.remove(w);
    }
    
    public boolean fullRemove(FBMenuItem item) {
        return super.remove(item);
    }
}
