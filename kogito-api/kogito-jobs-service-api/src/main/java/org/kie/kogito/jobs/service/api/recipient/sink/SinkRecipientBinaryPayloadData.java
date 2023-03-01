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

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

@Schema(allOf = { SinkRecipientPayloadData.class })
public class SinkRecipientBinaryPayloadData extends SinkRecipientPayloadData<byte[]> {

    @JsonProperty("data")
    private byte[] dataBytes;

    public SinkRecipientBinaryPayloadData() {
        // Marshalling constructor.
    }

    private SinkRecipientBinaryPayloadData(byte[] data) {
        this.dataBytes = data;
    }

    @Override
    public byte[] getData() {
        return dataBytes;
    }

    public static SinkRecipientBinaryPayloadData from(byte[] data) {
        return new SinkRecipientBinaryPayloadData(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SinkRecipientBinaryPayloadData that = (SinkRecipientBinaryPayloadData) o;
        return Arrays.equals(dataBytes, that.dataBytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(dataBytes);
    }
}
