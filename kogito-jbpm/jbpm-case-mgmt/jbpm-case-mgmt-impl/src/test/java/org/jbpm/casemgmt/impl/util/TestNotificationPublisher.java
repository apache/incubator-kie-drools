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

package org.jbpm.casemgmt.impl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.utils.NotificationPublisher;


public class TestNotificationPublisher implements NotificationPublisher {

    private List<String> published = new ArrayList<>();
    private boolean templateNotSupported = false;
            
    public TestNotificationPublisher(boolean templateNotSupported) {
        this.templateNotSupported = templateNotSupported;
    }
    
    @Override
    public void publish(String sender, String subject, Set<OrganizationalEntity> recipients, String body) {                    
        String notificationString = String.format("Publishing notification from %s, with subject %s to %s with body %s", sender, subject, recipients, body);
        published.add(notificationString);
    }

    @Override
    public void publish(String sender, String subject, Set<OrganizationalEntity> recipients, String template, Map<String, Object> parameters) {            
        if (templateNotSupported) {
            throw new IllegalArgumentException("Tempalate " + template + " not found");
        }
        String notificationString = String.format("Publishing notification from %s, with subject %s to %s with template %s", sender, subject, recipients, template);
        published.add(notificationString);
    }
    
    public List<String> get() {
        List<String> current = new ArrayList<>(this.published);
        
        this.published.clear();
        
        return current;
    }

    @Override
    public boolean isActive() {
        return true;
    }

}
