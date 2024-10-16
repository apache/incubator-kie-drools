/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.serverless.workflow;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.serverless.workflow.WorkflowWorkItemHandler.safeCast;

public class WorkflowWorkItemHandlerTest {

    @Test
    void testSafeCast() {
        assertThat(safeCast(4, Long.class)).isInstanceOf(Long.class).isEqualTo(4);
        assertThat(safeCast(4L, Integer.class)).isInstanceOf(Integer.class).isEqualTo(4);
        assertThat(safeCast(4, Float.class)).isInstanceOf(Float.class).isEqualTo(4);
        assertThat(safeCast(4, Double.class)).isInstanceOf(Double.class).isEqualTo(4);
        assertThat(safeCast(1.5f, Long.class)).isInstanceOf(Long.class).isEqualTo(1);
        assertThat(safeCast(1.5f, Integer.class)).isInstanceOf(Integer.class).isEqualTo(1);
        assertThat(safeCast(1.5, Float.class)).isInstanceOf(Float.class).isEqualTo(1.5f);
        assertThat(safeCast(1.5f, Double.class)).isInstanceOf(Double.class).isEqualTo(1.5);
    }
}
