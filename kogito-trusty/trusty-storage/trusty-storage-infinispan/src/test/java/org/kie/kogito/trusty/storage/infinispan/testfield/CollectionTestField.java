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
package org.kie.kogito.trusty.storage.infinispan.testfield;

import java.io.IOException;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.infinispan.protostream.MessageMarshaller;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;

public class CollectionTestField<M, T> extends AbstractTestField<M, Collection<T>> {

    private final Class<T> elementClass;

    public CollectionTestField(String fieldName, Collection<T> fieldValue, Function<M, Collection<T>> getter, BiConsumer<M, Collection<T>> setter, Class<T> elementClass) {
        super(fieldName, fieldValue, getter, setter);
        this.elementClass = elementClass;
    }

    @Override
    protected Collection<T> callMockReaderMethod(MessageMarshaller.ProtoStreamReader mock) throws IOException {
        return mock.readCollection(eq(fieldName), anyCollection(), eq(elementClass));
    }

    @Override
    protected void callVerifyWriterMethod(MessageMarshaller.ProtoStreamWriter mock) throws IOException {
        mock.writeCollection(eq(fieldName), eq(fieldValue), eq(elementClass));
    }
}
