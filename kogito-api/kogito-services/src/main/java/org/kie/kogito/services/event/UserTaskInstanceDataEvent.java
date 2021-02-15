/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.services.event;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.kie.kogito.event.AbstractDataEvent;
import org.kie.kogito.services.event.impl.ProcessInstanceEventBody;
import org.kie.kogito.services.event.impl.UserTaskInstanceEventBody;

public class UserTaskInstanceDataEvent extends AbstractDataEvent<UserTaskInstanceEventBody> {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String kogitoUserTaskinstanceId;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final String kogitoUserTaskinstanceState;

    public UserTaskInstanceDataEvent(String source, String addons, Map<String, String> metaData, UserTaskInstanceEventBody body) {

        super("UserTaskInstanceEvent",
              source,
              body,
              metaData.get(ProcessInstanceEventBody.ID_META_DATA),
              metaData.get(ProcessInstanceEventBody.ROOT_ID_META_DATA),
              metaData.get(ProcessInstanceEventBody.PROCESS_ID_META_DATA),
              metaData.get(ProcessInstanceEventBody.ROOT_PROCESS_ID_META_DATA),
              addons
        );

        this.kogitoUserTaskinstanceState = metaData.get(UserTaskInstanceEventBody.UT_STATE_META_DATA);
        this.kogitoUserTaskinstanceId = metaData.get(UserTaskInstanceEventBody.UT_ID_META_DATA);
    }

    public String getKogitoUserTaskinstanceId() {
        return kogitoUserTaskinstanceId;
    }

    public String getKogitoUserTaskinstanceState() {
        return kogitoUserTaskinstanceState;
    }
}
