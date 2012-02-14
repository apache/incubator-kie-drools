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

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.menu.FBMenuItem;
import org.jbpm.formbuilder.client.RoleUtils;
import org.jbpm.formbuilder.client.bus.MenuItemAddedEvent;
import org.jbpm.formbuilder.client.bus.MenuItemAddedHandler;
import org.jbpm.formbuilder.client.bus.MenuItemFromServerEvent;
import org.jbpm.formbuilder.client.bus.MenuItemFromServerHandler;
import org.jbpm.formbuilder.client.bus.MenuItemRemoveEvent;
import org.jbpm.formbuilder.client.bus.MenuItemRemoveHandler;
import org.jbpm.formbuilder.client.menu.items.CustomMenuItem;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.shared.EventBus;

/**
 * Menu presenter. Handles the adding and
 * removing of items from the view when
 * notified, either from the current user
 * or from the server.
 */
public class MenuPresenter {

    private final EventBus bus;
    private final MenuView view;
    private final PickupDragController dragController;
    
    public MenuPresenter(MenuView menuView) {
        super();
        this.view = menuView;
        this.bus = CommonGlobals.getInstance().getEventBus();
        this.dragController = CommonGlobals.getInstance().getDragController();
        this.view.startDropController(this.dragController);

        this.bus.addHandler(MenuItemAddedEvent.TYPE, new MenuItemAddedHandler() {
            @Override
            public void onEvent(MenuItemAddedEvent event) {
            	String group = event.getGroupName();
                FBMenuItem item = event.getMenuItem();
                if (RoleUtils.getInstance().hasDesignPrivileges()) {
            		view.addItem(group, item);
            	} else if (RoleUtils.getInstance().hasOnlyUserPrivileges()) {
            		if (item instanceof CustomMenuItem) {
            			view.addItem(group, item);
            		}
            	} 
            }
        });
        this.bus.addHandler(MenuItemRemoveEvent.TYPE, new MenuItemRemoveHandler() {
            @Override
            public void onEvent(MenuItemRemoveEvent event) {
                String group = event.getGroupName();
                FBMenuItem item = event.getMenuItem();
                view.removeItem(group, item);
            }
        });
        this.bus.addHandler(MenuItemFromServerEvent.TYPE, new MenuItemFromServerHandler() {
            @Override
            public void onEvent(MenuItemFromServerEvent event) {
                String group = event.getGroupName();
                FBMenuItem item = event.getMenuItem();
                if (RoleUtils.getInstance().hasDesignPrivileges()) {
                	view.addItem(group, item);
                } else if (RoleUtils.getInstance().hasOnlyUserPrivileges()) {
                	if (item instanceof CustomMenuItem) {
                		view.addItem(group, item);
                	}
                }
            }
        });
    }
}
