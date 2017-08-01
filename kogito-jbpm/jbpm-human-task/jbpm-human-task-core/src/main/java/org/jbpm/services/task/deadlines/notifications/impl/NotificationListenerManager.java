/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task.deadlines.notifications.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.jbpm.services.task.deadlines.NotificationListener;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.model.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages broadcasting of notification events to all found listeners 
 *
 */
public class NotificationListenerManager {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationListenerManager.class);
    
    private static ServiceLoader<NotificationListener> listenersLoaded = ServiceLoader.load(NotificationListener.class);
    
    private static NotificationListenerManager INSTANCE = new NotificationListenerManager();
    
    private List<NotificationListener> listeners = new ArrayList<NotificationListener>();
    
    private NotificationListenerManager() {
        for (NotificationListener listener : listenersLoaded) {
            listeners.add(listener);
        }        
    }

    /**
     * Broadcast given event to all listeners independently meaning catches possible exceptions to 
     * avoid breaking notification by listeners
     * @param event notification event to be sent
     * @param params additional parameters see NotificationListener.onNotification for details.
     * 
     * @see NotificationListener#onNotification(NotificationEvent, Object...)
     */
    public void broadcast(NotificationEvent event, UserInfo userInfo) {
        
        for (NotificationListener listener : listeners) {
            try {
                logger.debug("Sending notification {} to {} with params {}", event, listener, userInfo);
                listener.onNotification(event, userInfo);
            } catch (Exception e) {
                logger.warn("Exception encountered while notifying listener {} with event {} - error {}", 
                        listener, event, e.getMessage());
            }
        }
    }
    
    public static NotificationListenerManager get() {        
        return INSTANCE;
    }
}
