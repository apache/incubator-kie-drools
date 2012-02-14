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
package org.jbpm.formbuilder.client.notification;

import org.jbpm.formapi.client.CommonGlobals;
import org.jbpm.formapi.client.bus.ui.NotificationEvent;
import org.jbpm.formapi.client.bus.ui.NotificationHandler;

import com.google.gwt.event.shared.EventBus;

/**
 * Notifications presenter. Adds messages to
 * the view when a notification happens
 */
public class NotificationsPresenter implements NotificationsView.Presenter {

    private final NotificationsView view;
    private final EventBus bus = CommonGlobals.getInstance().getEventBus();

    public NotificationsPresenter(NotificationsView notifView) {
        this.view = notifView;
        bus.addHandler(NotificationEvent.TYPE, new NotificationHandler() {
            @Override
            public void onEvent(NotificationEvent event) {
                String colorCss = view.getColorCss(event.getLevel().name());
                String message = event.getMessage();
                Throwable error = event.getError();
                view.append(colorCss, message, error);
            }
        });
    }
    
}
