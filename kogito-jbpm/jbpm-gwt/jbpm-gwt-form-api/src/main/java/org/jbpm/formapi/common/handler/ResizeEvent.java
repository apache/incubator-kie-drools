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

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * Notifies a widget has a new size
 */
public class ResizeEvent extends GwtEvent<ResizeEventHandler> {

    public static final Type<ResizeEventHandler> TYPE = new Type<ResizeEventHandler>();
    
    private final Widget widget;
    private final int width;
    private final int height;
    
    public ResizeEvent(Widget widget, int width, int height) {
        this.widget = widget;
        this.width = width;
        this.height = height;
    }
    
    public Widget getWidget() {
        return widget;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    @Override
    public Type<ResizeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ResizeEventHandler handler) {
        handler.onResize(this);
    }

}
