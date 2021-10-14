/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.api.definition.process;


public enum NodeType {
    SCRIPT_TASK,
    HUMAN_TASK,
    WORKITEM_TASK,
    MILESTONE,
    THROW_EVENT,
    CATCH_EVENT,
    BOUNDARY_EVENT,
    AD_HOC_SUBPROCESS,
    EVENT_SUBPROCESS,
    SUBPROCESS,
    COMPLEX_GATEWAY,
    PARALLEL_GATEWAY,
    INCLUSIVE_GATEWAY,
    EXCLUSIVE_GATEWAY,
    EVENT_BASED_GATEWAY,
    START,
    END,
    CONDITIONAL,
    FOR_EACH,
    CATCH_LINK,
    THROW_LINK,
    FAULT,
    TIMER,
    BUSINESS_RULE,
    INTERNAL
}
