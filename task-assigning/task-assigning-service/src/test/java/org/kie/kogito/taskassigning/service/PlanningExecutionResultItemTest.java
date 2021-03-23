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

package org.kie.kogito.taskassigning.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.core.model.Task;

import static org.assertj.core.api.Assertions.assertThat;

class PlanningExecutionResultItemTest {

    private PlanningItem planningItem;
    private Exception error;
    private PlanningExecutionResultItem resultItem;

    @BeforeEach
    void setUp() {
        planningItem = new PlanningItem(Task.newBuilder().build(), "target_user");
        error = new Exception("some exception");
        resultItem = new PlanningExecutionResultItem(planningItem, error);
    }

    @Test
    void getItem() {
        assertThat(resultItem.getItem()).isSameAs(planningItem);
    }

    @Test
    void getError() {
        assertThat(resultItem.getError()).isSameAs(error);
    }

    @Test
    void hasErrorTrue() {
        assertThat(resultItem.hasError()).isTrue();
    }

    @Test
    void hasErrorFalse() {
        resultItem = new PlanningExecutionResultItem(planningItem);
        assertThat(resultItem.hasError()).isFalse();
    }
}
