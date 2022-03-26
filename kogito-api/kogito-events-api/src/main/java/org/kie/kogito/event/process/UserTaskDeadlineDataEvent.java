/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.event.process;

import org.kie.kogito.event.AbstractDataEvent;

public class UserTaskDeadlineDataEvent extends AbstractDataEvent<UserTaskDeadlineEventBody> {

    public UserTaskDeadlineDataEvent() {
    }

    public UserTaskDeadlineDataEvent(String type, String source, String addons,
            UserTaskDeadlineEventBody body, String processInstanceId,
            String rootProcessInstanceId, String processId, String rootProcessId) {
        super(type,
                source,
                body,
                processInstanceId,
                rootProcessInstanceId,
                processId,
                rootProcessId,
                addons);
    }

}
