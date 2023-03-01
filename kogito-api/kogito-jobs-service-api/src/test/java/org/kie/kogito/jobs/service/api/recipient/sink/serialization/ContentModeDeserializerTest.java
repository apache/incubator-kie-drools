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

package org.kie.kogito.jobs.service.api.recipient.sink.serialization;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.jobs.service.api.recipient.sink.SinkRecipient;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ContentModeDeserializerTest {

    @Mock
    private JsonParser parser;

    @Mock
    private DeserializationContext context;

    private ContentModeDeserializer deserializer = new ContentModeDeserializer();

    @Test
    void deserializeStructured() throws IOException {
        doReturn("structured").when(parser).getText();
        assertThat(deserializer.deserialize(parser, context)).isEqualTo(SinkRecipient.ContentMode.STRUCTURED);
    }

    @Test
    void deserializeBinary() throws IOException {
        doReturn("binary").when(parser).getText();
        assertThat(deserializer.deserialize(parser, context)).isEqualTo(SinkRecipient.ContentMode.BINARY);
    }

    @Test
    void deserializeUnknown() throws IOException {
        doReturn("unknown").when(parser).getText();
        assertThatThrownBy(() -> deserializer.deserialize(parser, context))
                .hasMessage("Invalid content mode: unknown");
    }
}
