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

import org.jbpm.services.task.deadlines.NotificationListener;
import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.model.NotificationEvent;

/**
 *
 */

public class MockNotificationListener implements NotificationListener{
    private static List<NotificationEvent> eventsRecieved = new ArrayList<NotificationEvent>();

    public MockNotificationListener() {
        eventsRecieved = new ArrayList<NotificationEvent>();
    }
    
    
    public void onNotification(NotificationEvent event, UserInfo userInfo) {
        eventsRecieved.add(event);
    }

    public  List<NotificationEvent> getEventsRecieved() {
        return eventsRecieved;
    }
    
    public void reset(){
        eventsRecieved = new ArrayList<NotificationEvent>();
    }
    
    
    
}
