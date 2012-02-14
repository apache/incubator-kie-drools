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

import org.jbpm.formbuilder.client.options.MainMenuOption;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Notifies a new menu option has been registered
 */
public class MenuOptionAddedEvent extends GwtEvent<MenuOptionAddedHandler> {

    public static final Type<MenuOptionAddedHandler> TYPE = new Type<MenuOptionAddedHandler>();
    
    private final MainMenuOption option;
    
    public MenuOptionAddedEvent(MainMenuOption option) {
        super();
        this.option = option;
    }

    public MainMenuOption getOption() {
        return option;
    }
    
    @Override
    public Type<MenuOptionAddedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(MenuOptionAddedHandler handler) {
        handler.onEvent(this);
    }

}
