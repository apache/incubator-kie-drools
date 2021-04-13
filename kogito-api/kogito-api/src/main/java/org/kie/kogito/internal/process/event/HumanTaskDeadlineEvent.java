/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.internal.process.event;

import java.util.Map;

import org.kie.api.event.process.ProcessEvent;
import org.kie.kogito.process.workitem.HumanTaskWorkItem;

/**
 * An event when a dealine for task has expired
 */
public interface HumanTaskDeadlineEvent
        extends
        ProcessEvent {

    enum DeadlineType {
        Started,
        Completed
    }

    /**
     * Returns work item which timeout expires
     * 
     * @return work item
     */
    HumanTaskWorkItem getWorkItem();

    /**
     * Returns notification data
     * 
     * @return key-value pair list
     */
    Map<String, Object> getNotification();

    /**
     * Returns dealine type
     * 
     * @return not started or not completed
     */
    DeadlineType getType();
}