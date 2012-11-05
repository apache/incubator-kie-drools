/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.task.deadlines.notifications.impl;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import org.jbpm.task.deadlines.NotificationListener;
import org.jbpm.task.events.NotificationEvent;

/**
 *
 */
@ApplicationScoped
public class MockNotificationListener implements NotificationListener{
    private  List<NotificationEvent> eventsRecieved = new ArrayList<NotificationEvent>();

    public MockNotificationListener() {
    }
    
    
    public void onNotification(@Observes(notifyObserver = Reception.IF_EXISTS) NotificationEvent event) {
        eventsRecieved.add(event);
    }

    public  List<NotificationEvent> getEventsRecieved() {
        return eventsRecieved;
    }
    
    public void reset(){
        eventsRecieved = new ArrayList<NotificationEvent>();
    }
    
    
    
}
