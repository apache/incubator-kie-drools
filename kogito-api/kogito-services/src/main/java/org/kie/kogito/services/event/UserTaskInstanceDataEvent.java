/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.services.event;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.services.event.impl.ProcessInstanceEventBody;
import org.kie.kogito.services.event.impl.UserTaskInstanceEventBody;

public class UserTaskInstanceDataEvent implements DataEvent<UserTaskInstanceEventBody> {

    private final String specversion;
    private final String id;
    private final String source;
    private final String type;
    private final String time;
    private final UserTaskInstanceEventBody data;

    private final String kogitoUserTaskinstanceId;
    private final String kogitoProcessinstanceId;
    private final String kogitoRootProcessinstanceId;
    private final String kogitoProcessId;
    private final String kogitoRootProcessId;
    private final String kogitoUserTaskinstanceState;

    public UserTaskInstanceDataEvent(String source, Map<String, String> metaData, UserTaskInstanceEventBody body) {
        this.specversion = "0.3";
        this.id = UUID.randomUUID().toString();
        this.source = source;
        this.type = "UserTaskInstanceEvent";
        this.time = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        this.data = body;

        this.kogitoProcessinstanceId = metaData.get(ProcessInstanceEventBody.ID_META_DATA);
        this.kogitoRootProcessinstanceId = metaData.get(ProcessInstanceEventBody.ROOT_ID_META_DATA);
        this.kogitoProcessId = metaData.get(ProcessInstanceEventBody.PROCESS_ID_META_DATA);
        this.kogitoRootProcessId = metaData.get(ProcessInstanceEventBody.ROOT_PROCESS_ID_META_DATA);

        this.kogitoUserTaskinstanceState = metaData.get(UserTaskInstanceEventBody.UT_STATE_META_DATA);
        this.kogitoUserTaskinstanceId = metaData.get(UserTaskInstanceEventBody.UT_ID_META_DATA);
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public UserTaskInstanceEventBody getData() {
        return data;
    }

    @Override
    public String getSpecversion() {
        return specversion;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getTime() {
        return time;
    }

    public String getKogitoProcessinstanceId() {
        return kogitoProcessinstanceId;
    }

    public String getKogitoRootProcessinstanceId() {
        return kogitoRootProcessinstanceId;
    }

    public String getKogitoProcessId() {
        return kogitoProcessId;
    }

    public String getKogitoRootProcessId() {
        return kogitoRootProcessId;
    }

    public String getKogitoUserTaskinstanceId() {
        return kogitoUserTaskinstanceId;
    }

    public String getKogitoUserTaskinstanceState() {
        return kogitoUserTaskinstanceState;
    }

}
