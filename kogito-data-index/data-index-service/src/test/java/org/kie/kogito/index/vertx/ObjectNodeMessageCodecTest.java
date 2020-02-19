/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.vertx;

import java.io.IOException;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vertx.core.buffer.Buffer;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.json.DataIndexParsingException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObjectNodeMessageCodecTest {

    @Test
    public void testEncodeToWireException() {
        Buffer buffer = mock(Buffer.class);
        when(buffer.appendBytes(any())).thenAnswer(args -> {
            throw new IOException();
        });

        assertThrows(DataIndexParsingException.class,
                     () -> new ObjectNodeMessageCodec().encodeToWire(buffer, mock(ObjectNode.class)));
    }

    @Test
    public void testDecodeFromWireException() {
        Buffer buffer = mock(Buffer.class);
        when(buffer.getBytes()).thenAnswer(args -> {
            throw new IOException();
        });

        assertThrows(DataIndexParsingException.class,
                     () -> new ObjectNodeMessageCodec().decodeFromWire(0, buffer));
    }
}
