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
package org.jbpm.formbuilder.client.tree;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.form.FBCompositeItem;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formbuilder.client.bus.ui.FormItemAddedEvent;
import org.jbpm.formbuilder.client.bus.ui.FormItemAddedHandler;
import org.jbpm.formbuilder.client.bus.ui.FormItemRemovedEvent;
import org.jbpm.formbuilder.client.bus.ui.FormItemRemovedHandler;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;

/**
 * Tree presenter. Handles notifications of form items added and removed
 * and tells the view to update itself
 */
public class TreePresenter implements TreeView.Presenter {

    private final TreeView view;
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();
    
    public TreePresenter(TreeView treeView) {
        this.view = treeView;
        bus.addHandler(FormItemRemovedEvent.TYPE, new FormItemRemovedHandler() {
            @Override
            public void onEvent(FormItemRemovedEvent event) {
                FBFormItem item = event.getFormItem();
                view.removeFormItem(item);
            }
        });
        bus.addHandler(FormItemAddedEvent.TYPE, new FormItemAddedHandler() {
            @Override
            public void onEvent(FormItemAddedEvent event) {
                FBFormItem item = event.getFormItem();
                Widget parent = event.getFormItemHolder();
                FBCompositeItem parentItem = null;
                while (parent != null && parentItem == null) {
                    if (parent instanceof FBCompositeItem) {
                        parentItem = (FBCompositeItem) parent;
                    } else {
                        parent = parent.getParent();
                    }
                }
                view.addFormItem(item, parentItem);
            }
        });
    }
}
