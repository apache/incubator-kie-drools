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
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.infinispan.protostream.MessageMarshaller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractTestField<M, T> {

    protected final String fieldName;
    protected final T fieldValue;
    private final Function<M, T> getter;
    private final BiConsumer<M, T> setter;

    public AbstractTestField(String fieldName, T fieldValue, Function<M, T> getter, BiConsumer<M, T> setter) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.getter = getter;
        this.setter = setter;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setValue(M object) {
        setter.accept(object, fieldValue);
    }

    public void assertValue(M object) {
        assertEquals(fieldValue, getter.apply(object));
    }

    public void mockReader(MessageMarshaller.ProtoStreamReader mock) throws IOException {
        when(callMockReaderMethod(mock)).thenReturn(fieldValue);
    }

    protected abstract T callMockReaderMethod(MessageMarshaller.ProtoStreamReader mock) throws IOException;

    public void verifyWriter(MessageMarshaller.ProtoStreamWriter mock) throws IOException {
        callVerifyWriterMethod(verify(mock));
    }

    protected abstract void callVerifyWriterMethod(MessageMarshaller.ProtoStreamWriter mock) throws IOException;
}
