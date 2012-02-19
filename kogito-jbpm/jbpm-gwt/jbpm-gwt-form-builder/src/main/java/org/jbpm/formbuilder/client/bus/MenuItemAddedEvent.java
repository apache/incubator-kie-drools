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
 * notifies a new menu item has been added
 */
public class MenuItemAddedEvent extends GwtEvent<MenuItemAddedHandler> {

    public static final Type<MenuItemAddedHandler> TYPE = new Type<MenuItemAddedHandler>();
    
    private final FBMenuItem menuItem;
    private final String groupName;
    
    public MenuItemAddedEvent(FBMenuItem menuItem, String groupName) {
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
    public Type<MenuItemAddedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(MenuItemAddedHandler handler) {
        handler.onEvent(this);
    }

}
