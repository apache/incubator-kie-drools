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

import org.jbpm.formapi.client.menu.FBMenuItem;

import com.google.gwt.event.shared.GwtEvent;

/**
 * notifies that a new menu item has been parsed from the server and must be added to the client
 */
public class MenuItemFromServerEvent extends GwtEvent<MenuItemFromServerHandler> {

    public static final Type<MenuItemFromServerHandler> TYPE = new Type<MenuItemFromServerHandler>();
    
    private final FBMenuItem menuItem;
    private final String groupName;
    
    public MenuItemFromServerEvent(FBMenuItem menuItem, String groupName) {
        this.menuItem = menuItem;
        this.groupName = groupName;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public FBMenuItem getMenuItem() {
        return menuItem;
    }
    
    @Override
    public Type<MenuItemFromServerHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(MenuItemFromServerHandler handler) {
        handler.onEvent(this);
    }

}
