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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwt.mosaic.ui.client.layout.BoxLayout;
import org.gwt.mosaic.ui.client.layout.BoxLayoutData;
import org.gwt.mosaic.ui.client.layout.LayoutPanel;
import org.jbpm.formapi.client.menu.FBMenuItem;
import org.jbpm.formbuilder.client.command.DisposeDropController;

import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.Widget;

public class AnimatedMenuViewImpl extends ScrollPanel implements MenuView {

    private PickupDragController dragController;
    
    private Map<String, List<FBMenuItem>> items = new HashMap<String, List<FBMenuItem>>();
    private Map<String, FBMenuPanel> displays = new HashMap<String, FBMenuPanel>();
    
    private StackPanel panel = new StackPanel() {
        @Override
        public void showStack(int index) {
            super.showStack(index);
            FBMenuPanel panel = (FBMenuPanel) getWidget(index);
            for (Widget widget : panel) {
                dragController.makeDraggable(widget);
            }
        };
    };
    
    public AnimatedMenuViewImpl() {
        LayoutPanel layoutPanel = new LayoutPanel(new BoxLayout(BoxLayout.Orientation.VERTICAL));
        layoutPanel.setLayoutData(new BoxLayoutData(BoxLayoutData.FillStyle.BOTH));
        layoutPanel.setAnimationEnabled(true);
        panel.setStylePrimaryName("fbStackPanel");
        layoutPanel.add(panel);
        add(layoutPanel);
        
        new MenuPresenter(this);
    }
    
    @Override
    public void startDropController(PickupDragController dragController) {
        this.dragController = dragController;
        this.dragController.registerDropController(new DisposeDropController(this));
        
        this.dragController.setBehaviorMultipleSelection(false);
        this.dragController.setConstrainWidgetToBoundaryPanel(false);
        this.dragController.addDragHandler(new DragHandlerAdapter());
    }

    @Override
    public void addItem(String group, FBMenuItem item) {
        if (items.get(group) == null) {
            items.put(group, new ArrayList<FBMenuItem>());
            FBMenuPanel listDisplay = new FBMenuPanel(dragController);
            panel.add(listDisplay, group);
            displays.put(group, listDisplay);
        }
        this.displays.get(group).add(item);
        this.items.get(group).add(item);
    }

    @Override
    public void removeItem(String group, FBMenuItem item) {
        List<FBMenuItem> groupItems = items.get(group);
        if (groupItems != null) {
            groupItems.remove(item);
            FBMenuPanel display = displays.get(group);
            display.fullRemove(item);
            if (groupItems.isEmpty()) {
                panel.remove(display);
                panel.showStack(0);
            }
        }
    }

}
