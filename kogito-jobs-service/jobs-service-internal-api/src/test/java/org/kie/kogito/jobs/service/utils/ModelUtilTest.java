/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.service.utils;

import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.TemporalUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ModelUtilTest {

    private Job job = Job.builder().build();

    @Test
    void getExecutionTimeoutInMillisNoValue() {
        assertThat(ModelUtil.getExecutionTimeoutInMillis(job)).isNull();
    }

    @Test
    void getExecutionTimeoutInMillisWithValue() {
        job.setExecutionTimeout(10L);
        assertThat(ModelUtil.getExecutionTimeoutInMillis(job)).isEqualTo(10L);
    }

    @Test
    void getExecutionTimeoutInMillisWithValueAndUnit() {
        job.setExecutionTimeout(5L);
        job.setExecutionTimeoutUnit(TemporalUnit.MINUTES);
        assertThat(ModelUtil.getExecutionTimeoutInMillis(job)).isEqualTo(300000L);
    }
}
