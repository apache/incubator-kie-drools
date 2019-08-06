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

public class ProcessInstanceDataEvent implements DataEvent<ProcessInstanceEventBody> {

    private final String specversion;
    private final String id;
    private final String source;
    private final String type;
    private final String time;
    private final ProcessInstanceEventBody data;

    private final String kogitoProcessinstanceId;
    private final String kogitoParentProcessinstanceId;
    private final String kogitoRootProcessinstanceId;
    private final String kogitoProcessId;
    private final String kogitoProcessinstanceState;

    public ProcessInstanceDataEvent(String source, Map<String, String> metaData, ProcessInstanceEventBody body) {
        this.specversion = "0.3";
        this.id = UUID.randomUUID().toString();
        this.source = source;
        this.type = "ProcessInstanceEvent";
        this.time = ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        this.data = body;

        this.kogitoProcessinstanceId = metaData.get(ProcessInstanceEventBody.ID_META_DATA);
        this.kogitoParentProcessinstanceId = metaData.get(ProcessInstanceEventBody.PARENT_ID_META_DATA);
        this.kogitoRootProcessinstanceId = metaData.get(ProcessInstanceEventBody.ROOT_ID_META_DATA);
        this.kogitoProcessId = metaData.get(ProcessInstanceEventBody.PROCESS_ID_META_DATA);
        this.kogitoProcessinstanceState = metaData.get(ProcessInstanceEventBody.STATE_META_DATA);
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public ProcessInstanceEventBody getData() {
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

    public String getKogitoParentProcessinstanceId() {
        return kogitoParentProcessinstanceId;
    }

    public String getKogitoRootProcessinstanceId() {
        return kogitoRootProcessinstanceId;
    }

    public String getKogitoProcessId() {
        return kogitoProcessId;
    }

    public String getKogitoProcessinstanceState() {
        return kogitoProcessinstanceState;
    }

}
