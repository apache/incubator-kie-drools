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

package org.kie.internal.utils;

import java.util.Map;
import java.util.Set;

import org.kie.api.task.model.OrganizationalEntity;

/**
 * Responsible for providing various types of publishers of notifications 
 * e.g. email, sms, message, etc.
 */
public interface NotificationPublisher {

    /**
     * Publishes given notification based on the actual implementation.
     * This method should never thrown exceptions as they might be interfering with processing logic. 
     * 
     * @param sender identifier of the sender to be attached to the message produced from the notification
     * @param subject subject to be used when publishing the notification
     * @param recipients list of users or groups the notification should be send to
     * @param body actual message body
     */
    void publish(String sender, String subject, Set<OrganizationalEntity> recipients, String body);
    
    /**
     * Renders body based on given template and parameters and then publishes given notification based on the actual implementation.
     * 
     * This method can throw IllegalArgumentException in case given template was not found. This allows to fall back 
     * to send notification with not rendered body 
     * 
     * @param sender identifier of the sender to be attached to the message produced from the notification
     * @param subject subject to be used when publishing the notification
     * @param recipients list of users or groups the notification should be send to
     * @param template name of the template to be used to render body
     * @param parameters map of parameters that can be used while rendering body from the template
     * @throws IllegalArgumentException when template was not found
     */
    void publish(String sender, String subject, Set<OrganizationalEntity> recipients, String template, Map<String, Object> parameters);
    
    /**
     * Indicates if given publisher is active and thus should be used to publish notifications
     * @return true if active false otherwise
     */
    boolean isActive();
}
