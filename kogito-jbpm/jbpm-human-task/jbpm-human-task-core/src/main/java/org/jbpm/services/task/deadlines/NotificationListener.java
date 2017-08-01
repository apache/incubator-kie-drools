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
package org.jbpm.services.task.deadlines;

import org.kie.internal.task.api.UserInfo;
import org.kie.internal.task.api.model.NotificationEvent;

/**
 * Listener that is called when Task notification is about to be sent.
 * Different listener implementations can exists and all will be independently invoked.
 */
public interface NotificationListener {
    
    /**
     * Invoked when notification is about to be sent.
     * @param event notification event that includes task, notification itself and task variables
     * @param userInfo Implementation of <code>UserInfo</code> to be able to find user details such as email address
     */
	void onNotification(NotificationEvent event, UserInfo userInfo);
}
