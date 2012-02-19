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
package org.jbpm.formbuilder.client.effect;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.common.panels.MovablePanel;
import org.jbpm.formbuilder.client.FormBuilderGlobals;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class MoveItemFormEffect extends FBFormEffect {

    private final PickupDragController dragController = CommonGlobals.getInstance().getDragController();
    
    public MoveItemFormEffect() {
        super(FormBuilderGlobals.getInstance().getI18n().MoveItemEffectLabel(), false);
    }

    @Override
    protected void createStyles() {
        final FBFormItem item = getItem();
        final Widget actualWidget = item.getWidget();
        MovablePanel movable = new MovablePanel(actualWidget, item);
        dragController.makeDraggable(movable);
        dragController.addDragHandler(new DragHandlerAdapter() {
            @Override
            public void onDragEnd(DragEndEvent event) {
                item.clear();
                item.setWidget(actualWidget);
            }
        });
        item.clear();
        item.setWidget(movable);
    }
}
