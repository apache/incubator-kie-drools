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

package org.kie.kogito.jobs.api.event;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractProcessInstanceContextJobCloudEventTest<E extends ProcessInstanceContextJobCloudEvent<?>> extends AbstractJobCloudEventTest<E> {

    static final String PROCESS_ID = "PROCESS_ID";
    static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    static final String ROOT_PROCESS_INSTANCE_ID = "ROOT_PROCESS_INSTANCE_ID";
    static final String ROOT_PROCESS_ID = "ROOT_PROCESS_ID";
    static final String ADDONS = "ADDONS";

    @Override
    void assertFields(E event) {
        super.assertFields(event);
        assertThat(event.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(event.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(event.getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(event.getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(event.getKogitoAddons()).isEqualTo(ADDONS);
    }
}
