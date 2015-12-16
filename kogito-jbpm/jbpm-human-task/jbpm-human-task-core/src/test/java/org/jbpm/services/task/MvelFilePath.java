/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.services.task;

public class MvelFilePath {

    public static final String DeadlineWithReassignment = "/org/jbpm/services/task/service/DeadlineWithReassignment.mvel";
    public static final String DeadlineWithNotification = "/org/jbpm/services/task/service/DeadlineWithNotification.mvel";
    public static final String DeadlineWithNotificationContentSingleObject = "/org/jbpm/services/task/service/DeadlineWithNotificationContentSingleObject.mvel";
    
    public static final String FullTask = "/org/jbpm/services/task/xml/JaxbFullTask.mvel";
    
    public static final String ReminderWithNotificationReserved = "/org/jbpm/services/task/service/ReminderWithNotificationReserved.mvel";
    public static final String ReminderWithNotificationInProgress = "/org/jbpm/services/task/service/ReminderWithNotificationInProgress.mvel";
    public static final String ReminderWithoutNotification = "/org/jbpm/services/task/service/ReminderWithoutNotification.mvel";
}
