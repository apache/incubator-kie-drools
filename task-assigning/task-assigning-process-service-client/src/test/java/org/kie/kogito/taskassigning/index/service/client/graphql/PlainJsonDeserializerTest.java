/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.taskassigning.index.service.client.graphql;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PlainJsonDeserializerTest {

    private static final String RAW_VALUE = "{\"field1\": \"value1\", \"field2\": \"value2\"}";

    @Mock
    private JsonParser parser;

    @Mock
    private DeserializationContext ctx;

    @Test
    void deserialize() throws IOException {
        doReturn(RAW_VALUE).when(parser).getText();
        PlainJsonDeserializer deserializer = new PlainJsonDeserializer();
        JsonNode result = deserializer.deserialize(parser, ctx);
        assertThat(result.get("field1")).hasToString("\"value1\"");
        assertThat(result.get("field2")).hasToString("\"value2\"");
        assertThat(result.size()).isEqualTo(2);
    }
}
