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
package org.jbpm.formapi.common.handler;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.Event;

/**
 * Notifies when a right click event has happened
 */
public class RightClickEvent extends MouseEvent<RightClickHandler> {

    public static final Type<RightClickHandler> TYPE = new Type<RightClickHandler>("rclick", new RightClickEvent(null));
    
    private final Event event;
    
    public RightClickEvent(Event event) {
        this.event = event;
    }

    @Override
    public Type<RightClickHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RightClickHandler handler) {
        handler.onRightClick(this);
    }

    @Override
    public final int getClientX() {
        return event.getClientX();
    }

    @Override
    public final int getClientY() {
        return event.getClientY();
    }

    @Override
    public final int getScreenX() {
        return event.getScreenX();
    }

    @Override
    public final int getScreenY() {
        return event.getScreenY();
    }

    
    
}
