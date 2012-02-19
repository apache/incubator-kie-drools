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
package org.jbpm.formapi.client.bus.ui;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Notifies a loggable message for the UI
 */
public class NotificationEvent extends GwtEvent<NotificationHandler> {

    public static final Type<NotificationHandler> TYPE = new Type<NotificationHandler>();
    
    public static enum Level {
        INFO, WARN, ERROR;
    }
    
    private final Level level;
    private final String message;
    private final Throwable error;
    
    public NotificationEvent(String message) {
        this(Level.INFO, message, null);
    }
    
    public NotificationEvent(Level level, String message) {
        this(level, message, null);
    }
    
    public NotificationEvent(Level level, String message, Throwable error) {
        super();
        this.level = level;
        this.message = message;
        this.error = error;
    }

    public Throwable getError() {
        return error;
    }
    
    public Level getLevel() {
        return level;
    }
    
    public String getMessage() {
        return message;
    }

    @Override
    public Type<NotificationHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(NotificationHandler handler) {
        handler.onEvent(this);
    }
}
