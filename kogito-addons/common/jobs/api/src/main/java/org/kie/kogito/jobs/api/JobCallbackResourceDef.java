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

package org.kie.kogito.jobs.api;

import org.kie.kogito.jobs.ProcessInstanceJobDescription;
import org.kie.kogito.jobs.service.api.TemporalUnit;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient;
import org.kie.kogito.jobs.service.api.recipient.http.HttpRecipientStringPayloadData;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;

/**
 * Common definitions for add-ons implementations based on the Jobs Service to Runtime rest callback pattern.
 */
public class JobCallbackResourceDef {

    public static final String PROCESS_ID = "processId";

    public static final String PROCESS_INSTANCE_ID = "processInstanceId";

    public static final String ROOT_PROCESS_ID = "rootProcessId";

    public static final String ROOT_PROCESS_INSTANCE_ID = "rootProcessInstanceId";

    public static final String NODE_INSTANCE_ID = "nodeInstanceId";

    public static final String TIMER_ID = "timerId";

    public static final String LIMIT = "limit";

    public static final String LIMIT_DEFAULT_VALUE = "0";

    public static final String JOBS_CALLBACK_URI = "/management/jobs";

    public static final String JOBS_CALLBACK_POST_URI = "{" + PROCESS_ID + "}/instances/{" + PROCESS_INSTANCE_ID + "}/timers/{" + TIMER_ID + "}";

    private JobCallbackResourceDef() {
    }

    public static String buildCallbackURI(ProcessInstanceJobDescription description, String jobsCallbackEndpoint) {
        return URIBuilder.toURI(jobsCallbackEndpoint
                + JOBS_CALLBACK_URI + "/"
                + description.processId()
                + "/instances/"
                + description.processInstanceId()
                + "/timers/"
                + description.id())
                .toString();
    }

    public static org.kie.kogito.jobs.service.api.Job buildCallbackPatternJob(ProcessInstanceJobDescription description, String callback) {
        return org.kie.kogito.jobs.service.api.Job.builder()
                .id(description.id())
                .correlationId(description.id())
                .recipient(buildRecipient(description, callback))
                .schedule(buildSchedule(description))
                .build();
    }

    private static HttpRecipient<HttpRecipientStringPayloadData> buildRecipient(ProcessInstanceJobDescription description, String callback) {
        return HttpRecipient.builder()
                .forStringPayload()
                .url(callback)
                .header(PROCESS_ID, description.processId())
                .header(PROCESS_INSTANCE_ID, description.processInstanceId())
                .header(ROOT_PROCESS_ID, description.rootProcessId())
                .header(ROOT_PROCESS_INSTANCE_ID, description.rootProcessInstanceId())
                .header(NODE_INSTANCE_ID, description.nodeInstanceId())
                .build();
    }

    private static TimerSchedule buildSchedule(ProcessInstanceJobDescription description) {
        return TimerSchedule.builder()
                .startTime(description.expirationTime().get().toOffsetDateTime())
                .repeatCount(description.expirationTime().repeatLimit())
                .delay(description.expirationTime().repeatInterval())
                .delayUnit(TemporalUnit.MILLIS)
                .build();
    }
}