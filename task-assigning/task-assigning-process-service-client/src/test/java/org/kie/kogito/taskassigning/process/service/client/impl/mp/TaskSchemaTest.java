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

package org.kie.kogito.taskassigning.process.service.client.impl.mp;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskSchemaTest {

    private static final String PHASE1 = "PHASE1";
    private static final String PHASE2 = "PHASE2";

    private TaskSchema taskSchema;

    @BeforeEach
    void setUp() {
        taskSchema = new TaskSchema(Collections.singletonList(PHASE1));
    }

    @Test
    void getPhases() {
        assertThat(taskSchema.getPhases()).containsExactly(PHASE1);
    }

    @Test
    void setPhases() {
        taskSchema.setPhases(Collections.singletonList(PHASE2));
        assertThat(taskSchema.getPhases()).containsExactly(PHASE2);
    }
}
