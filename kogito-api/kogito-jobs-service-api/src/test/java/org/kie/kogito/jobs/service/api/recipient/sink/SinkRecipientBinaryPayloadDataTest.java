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

package org.kie.kogito.jobs.service.api.recipient.sink;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SinkRecipientBinaryPayloadDataTest {

    private static final byte[] TEST_DATA = "some data to test".getBytes();

    @Test
    void getData() {
        SinkRecipientBinaryPayloadData payloadData = SinkRecipientBinaryPayloadData.from(TEST_DATA);
        assertThat(payloadData.getData()).isEqualTo(TEST_DATA);
    }

    @Test
    void equalsMethod() {
        SinkRecipientBinaryPayloadData payloadData1 = SinkRecipientBinaryPayloadData.from(TEST_DATA);
        SinkRecipientBinaryPayloadData payloadData2 = SinkRecipientBinaryPayloadData.from(TEST_DATA);
        assertThat(payloadData1.getData()).isEqualTo(payloadData2.getData());
    }

    @Test
    void hashCodeMethod() {
        SinkRecipientBinaryPayloadData payloadData = SinkRecipientBinaryPayloadData.from(TEST_DATA);
        assertThat(payloadData.hashCode()).isEqualTo(Arrays.hashCode(TEST_DATA));
    }
}
