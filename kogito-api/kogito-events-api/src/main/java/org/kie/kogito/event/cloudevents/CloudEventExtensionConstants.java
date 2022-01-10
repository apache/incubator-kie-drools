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
package org.kie.kogito.event.cloudevents;

/**
 * Includes all naming required for Kogito CloudEvent constants.
 * Must respect the required naming from the CloudEvent Specification.
 *
 * @see <a href="https://github.com/cloudevents/spec/blob/v1.0.1/spec.md#attribute-naming-convention">Attribute Naming Convention</a>
 */
public final class CloudEventExtensionConstants {

    // whenever a new extension is added, please add to the CloudEventExtensionConstantsTest to verify the CE spec requirements

    public static final String PMML_FULL_RESULT = "kogitopmmlfullresult";
    public static final String PMML_MODEL_NAME = "kogitopmmlmodelname";
    public static final String PROCESS_INSTANCE_ID = "kogitoprocinstanceid";
    public static final String PROCESS_REFERENCE_ID = "kogitoprocrefid";
    public static final String PROCESS_INSTANCE_STATE = "kogitoprocist";
    public static final String PROCESS_ID = "kogitoprocid";
    public static final String PROCESS_PARENT_PROCESS_INSTANCE_ID = "kogitoparentprociid";
    public static final String PROCESS_ROOT_PROCESS_INSTANCE_ID = "kogitorootprociid";
    public static final String PROCESS_ROOT_PROCESS_ID = "kogitorootprocid";
    public static final String PROCESS_START_FROM_NODE = "kogitoprocstartfrom";
    public static final String PROCESS_USER_TASK_INSTANCE_ID = "kogitousertaskiid";
    public static final String PROCESS_USER_TASK_INSTANCE_STATE = "kogitousertaskist";
    public static final String RULE_UNIT_ID = "kogitoruleunitid";
    public static final String RULE_UNIT_QUERY = "kogitoruleunitquery";
    public static final String ADDONS = "kogitoaddons";

    private CloudEventExtensionConstants() {
        // utility class
    }

}
